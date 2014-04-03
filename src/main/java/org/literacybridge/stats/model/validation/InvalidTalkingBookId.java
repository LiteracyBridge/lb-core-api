package org.literacybridge.stats.model.validation;

public class InvalidTalkingBookId extends ValidationError {
  public final String talkingBookId;

  public InvalidTalkingBookId(String talkingBookId) {
    super("Invalid talkingBookId name: " + talkingBookId, INVALID_TALKING_BOOK_NAME);
    this.talkingBookId = talkingBookId;
  }
}
