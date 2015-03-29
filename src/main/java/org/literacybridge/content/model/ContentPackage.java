package org.literacybridge.content.model;

import java.util.List;

/**
 * Defines the user accessible content for a Profile.  This contains a description for
 * internal use, as well as an ordered list of playlists that will describe what content
 * shows up in the talking book menus.
 *
 * @author willpugh
 */
public class ContentPackage {

  String description;
  List<PlayList> playlists;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<PlayList> getPlaylists() {
    return playlists;
  }

  public void setPlaylists(List<PlayList> playlists) {
    this.playlists = playlists;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ContentPackage)) return false;

    ContentPackage that = (ContentPackage) o;

    if (description != null ? !description.equals(that.description) : that.description != null) return false;
    if (playlists != null ? !playlists.equals(that.playlists) : that.playlists != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = description != null ? description.hashCode() : 0;
    result = 31 * result + (playlists != null ? playlists.hashCode() : 0);
    return result;
  }
}
