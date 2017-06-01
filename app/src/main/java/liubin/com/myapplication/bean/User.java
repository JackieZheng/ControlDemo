package liubin.com.myapplication.bean;

public class User {

  private String id;
  /**
   * 用户昵称	另还有一个字段name，是用户真实姓名
   */
  private String nickname;
  /**
   * 用户头像
   */
  private String image;
  /**
   * 用户签名
   */
  private String signature;

  private int isFollow;

  private String reasonContent;

  /**
   * 原因相关的时间
   * 如私信、关注、评论、点赞的时间
   */
  private long reasonDate;

  /**
   * [客户端]
   */
  private int type;

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getReasonContent() {
    return reasonContent;
  }

  public void setReasonContent(String reasonContent) {
    this.reasonContent = reasonContent;
  }

  public long getReasonDate() {
    return reasonDate;
  }

  public void setReasonDate(long reasonDate) {
    this.reasonDate = reasonDate;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public int getIsFollow() {
    return isFollow;
  }

  public void setIsFollow(int isFollow) {
    this.isFollow = isFollow;
  }

  public boolean isFollowed() {
    return isFollow == 1;
  }
}
