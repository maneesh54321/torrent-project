package com.torrent.torrentmanager.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TorrentDownloadVO {
    private String magnetLink;
    private String downloadLocation;
    private String category;
}
