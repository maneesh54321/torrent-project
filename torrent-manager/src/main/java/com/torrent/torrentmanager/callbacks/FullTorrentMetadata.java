package com.torrent.torrentmanager.callbacks;

import bt.metainfo.Torrent;
import bt.runtime.BtClient;
import bt.torrent.TorrentSessionState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullTorrentMetadata {
    private Torrent fetchedTorrent;
    private TorrentSessionState torrentSessionState;
    private BtClient client;

    public String getTorrentId(){
        return this.fetchedTorrent.getTorrentId().toString();
    }
}
