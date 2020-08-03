package com.torrent.torrentmanager.repository;

import com.torrent.torrentmanager.model.TorrentDownloadInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TorrentDownloadInfoRepository extends JpaRepository<TorrentDownloadInfo, String> {

}
