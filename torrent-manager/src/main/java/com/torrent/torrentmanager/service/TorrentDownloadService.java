package com.torrent.torrentmanager.service;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.metainfo.Torrent;
import bt.runtime.BtClient;
import bt.runtime.Config;
import bt.torrent.TorrentSessionState;
import com.google.inject.Module;
import com.torrent.torrentmanager.callbacks.BoatTorrentFileSelector;
import com.torrent.torrentmanager.callbacks.FullTorrentMetadata;
import com.torrent.torrentmanager.model.TorrentDownloadInfo;
import com.torrent.torrentmanager.repository.TorrentDownloadInfoRepository;
import com.torrent.torrentmanager.vo.TorrentDownloadVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TorrentDownloadService {

    private static final Map<String, FullTorrentMetadata> runningJobs = new HashMap<>();

    private final TorrentDownloadInfoRepository downloadInfoRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public TorrentDownloadService(TorrentDownloadInfoRepository downloadInfoRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.downloadInfoRepository = downloadInfoRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void startTorrentDownload(TorrentDownloadVO torrentDownloadVO) {
        Objects.requireNonNull(torrentDownloadVO, "Torrent is null.");
//        TorrentUtil.validateTorrent(torrentDownloadVO);

//        enable multithreaded verification of torrent data
        Config config = new Config() {
            @Override
            public int getNumOfHashingThreads() {
                return Runtime.getRuntime().availableProcessors() * 2;
            }
        };

//		enable bootstrapping from public routers
        Module dhtModule = new DHTModule(new DHTConfig() {
            @Override
            public boolean shouldUseRouterBootstrap() {
                return true;
            }
        });

//  	get download directory
        Path targetDirectory = Paths.get(torrentDownloadVO.getDownloadLocation());

// 		create file system based backend for torrent data
        Storage storage = new FileSystemStorage(targetDirectory);

        FullTorrentMetadata fullTorrentMetadata = new FullTorrentMetadata();

// 		create client with a private runtime
        BtClient client = Bt.client()
                .config(config)
                .storage(storage)
                .afterTorrentFetched(torrent -> {
                    fullTorrentMetadata.setFetchedTorrent(torrent);
                    persistTorrentDownload(torrentDownloadVO, torrent);
                    sendTorrentInfo(torrent);
                    runningJobs.put(fullTorrentMetadata.getTorrentId(), fullTorrentMetadata);
                })
                .fileSelector(new BoatTorrentFileSelector(fullTorrentMetadata, simpMessagingTemplate))
                .magnet(torrentDownloadVO.getMagnetLink())
                .autoLoadModules()
                .module(dhtModule)
                .stopWhenDownloaded()
                .build();

// 		launch
        client.startAsync(torrentSessionState -> {
            fullTorrentMetadata.setTorrentSessionState(torrentSessionState);
            sendTorrentSessionState(torrentSessionState, fullTorrentMetadata.getTorrentId());
        }, 500);
        fullTorrentMetadata.setClient(client);
    }

    private void sendTorrentSessionState(TorrentSessionState torrentSessionState, String torrentId) {
        simpMessagingTemplate.convertAndSend("/topic/" + torrentId, torrentSessionState);
    }

    private void sendTorrentInfo(Torrent torrent){
        simpMessagingTemplate.convertAndSend("/topic/torrent", torrent.getTorrentId().toString());
    }

    private void persistTorrentDownload(TorrentDownloadVO torrentDownloadVO, Torrent torrent) {
        TorrentDownloadInfo torrentDownloadInfo = new TorrentDownloadInfo(
                torrent.getTorrentId().toString(),
                torrent.getName(),
                torrentDownloadVO.getDownloadLocation(),
                torrentDownloadVO.getCategory(),
                new Date()
        );
        downloadInfoRepository.save(torrentDownloadInfo);
    }

    public void pauseDownload(String torrentId) {
        runningJobs.get(torrentId).getClient().stop();
    }

    public void resumeTorrent(String torrentId){
        FullTorrentMetadata fullTorrentMetadata = runningJobs.get(torrentId);
        BtClient client = fullTorrentMetadata.getClient();
        Objects.requireNonNull(client, "Invalid torrent!!");
        if(!client.isStarted()){
            client.startAsync(torrentSessionState -> {
                fullTorrentMetadata.setTorrentSessionState(torrentSessionState);
                sendTorrentSessionState(torrentSessionState, fullTorrentMetadata.getTorrentId());
            }, 500);
        }
    }
}
