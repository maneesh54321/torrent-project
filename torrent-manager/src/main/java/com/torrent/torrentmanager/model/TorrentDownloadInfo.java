package com.torrent.torrentmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "torrent_download_info")
public class TorrentDownloadInfo {
    @Id
    @Column(name = "torrent_id")
    private String torrentId;

    @Column(name = "torrent_name")
    private String torrentName;

    @Column(name = "download_location")
    private String downloadLocation;

    @Column(name = "category")
    private String category;

    @Column(name = "creation_time")
    private Date creationTime;
}
