package com.torrent.torrentmanager.controller;

import com.torrent.torrentmanager.service.TorrentDownloadService;
import com.torrent.torrentmanager.vo.TorrentDownloadVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TorrentController {

    private final TorrentDownloadService torrentDownloadService;

    @Autowired
    public TorrentController(TorrentDownloadService torrentDownloadService) {
        this.torrentDownloadService = torrentDownloadService;
    }

    @MessageMapping("/download/start")
    public void startDownload(TorrentDownloadVO torrentDownloadVOInfo) {
        torrentDownloadService.startTorrentDownload(torrentDownloadVOInfo);
    }

    @MessageMapping("/download/stop")
    public void stopDownload(String torrentId) {
        torrentDownloadService.pauseDownload(torrentId);
    }

    @MessageMapping("/download/pause")
    public void pauseDownload(String torrentId) {
        torrentDownloadService.pauseDownload(torrentId);
    }

    @MessageMapping("/download/resume")
    public void resumeDownload(String torrentId) {
        torrentDownloadService.resumeTorrent(torrentId);
    }
}
