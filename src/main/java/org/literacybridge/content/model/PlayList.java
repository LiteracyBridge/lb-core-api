package org.literacybridge.content.model;

import java.util.List;

/**
 * Describes a single playlist, which maps to a single menu the end-user will see.
 *
 * Each Playlist contians it category, as well as a list of IDs that uniquely identify the content.
 *
 * @author willpugh
 */
public class PlayList {
  String        playlistCategory;
  List<String>  contentIds;

  public String getPlaylistCategory() {
    return playlistCategory;
  }

  public void setPlaylistCategory(String playlistCategory) {
    this.playlistCategory = playlistCategory;
  }

  public List<String> getContentIds() {
    return contentIds;
  }

  public void setContentIds(List<String> contentIds) {
    this.contentIds = contentIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayList)) return false;

    PlayList playList = (PlayList) o;

    if (contentIds != null ? !contentIds.equals(playList.contentIds) : playList.contentIds != null) return false;
    if (playlistCategory != null ? !playlistCategory.equals(playList.playlistCategory)
                                 : playList.playlistCategory != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = playlistCategory != null ? playlistCategory.hashCode() : 0;
    result = 31 * result + (contentIds != null ? contentIds.hashCode() : 0);
    return result;
  }
}
