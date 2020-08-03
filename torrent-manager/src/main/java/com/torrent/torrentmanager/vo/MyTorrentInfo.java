package com.torrent.torrentmanager.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyTorrentInfo {
    private String name;
    private Long downloaded;
    private long uploaded;
    private long totalSize;
}
