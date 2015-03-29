package org.literacybridge.content.model;

/**
 * Describes a profile
 */
public class Profile {

  String systemLangauge;
  String systemMenu;
  ContentPackage contentPackage;

  public String getSystemLangauge() {
    return systemLangauge;
  }

  public void setSystemLangauge(String systemLangauge) {
    this.systemLangauge = systemLangauge;
  }

  public String getSystemMenu() {
    return systemMenu;
  }

  public void setSystemMenu(String systemMenu) {
    this.systemMenu = systemMenu;
  }

  public ContentPackage getContentPackage() {
    return contentPackage;
  }

  public void setContentPackage(ContentPackage contentPackage) {
    this.contentPackage = contentPackage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Profile)) return false;

    Profile profile = (Profile) o;

    if (contentPackage != null ? !contentPackage.equals(profile.contentPackage) : profile.contentPackage != null)
      return false;
    if (systemLangauge != null ? !systemLangauge.equals(profile.systemLangauge) : profile.systemLangauge != null)
      return false;
    if (systemMenu != null ? !systemMenu.equals(profile.systemMenu) : profile.systemMenu != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = systemLangauge != null ? systemLangauge.hashCode() : 0;
    result = 31 * result + (systemMenu != null ? systemMenu.hashCode() : 0);
    result = 31 * result + (contentPackage != null ? contentPackage.hashCode() : 0);
    return result;
  }
}
