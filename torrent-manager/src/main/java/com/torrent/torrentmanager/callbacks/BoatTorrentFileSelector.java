package com.torrent.torrentmanager.callbacks;

import bt.metainfo.TorrentFile;
import bt.torrent.fileselector.SelectionResult;
import bt.torrent.fileselector.TorrentFileSelector;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoatTorrentFileSelector extends TorrentFileSelector {

    private final FullTorrentMetadata fullTorrentMetadata;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final Map<TorrentFile, Boolean> fileSelectionMap = new HashMap<>();

    public BoatTorrentFileSelector(FullTorrentMetadata fullTorrentMetadata, SimpMessagingTemplate simpMessagingTemplate) {
        this.fullTorrentMetadata = fullTorrentMetadata;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public List<SelectionResult> selectFiles(List<TorrentFile> files) {
        this.simpMessagingTemplate.convertAndSend("/torrent/" + fullTorrentMetadata.getTorrentId(), files);
        // TODO: Create fileSelectionMap somehow
        files.forEach(file -> fileSelectionMap.put(file, true));
        return super.selectFiles(files);
    }

    @Override
    protected SelectionResult select(TorrentFile file) {
        return fileSelectionMap.get(file) ? SelectionResult.select().build() : SelectionResult.skip();
    }
}
