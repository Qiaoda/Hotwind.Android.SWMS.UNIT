package com.ex.lib.ext.share.entry;

/**
 * @ClassName: ShareInfo
 * @Description: 分享参数对象集合
 * @author Aloneter
 * @date 2014-6-10 下午11:35:08
 * @Version 1.0
 * 
 */
public class ExKeyShare {

	public static final String FIELD_SHARE_QZONE = "QZone";
	public static final String FIELD_SHARE_SINAWEIBO = "SinaWeibo";
	public static final String FIELD_SHARE_WECHATMOMENTS = "WechatMoments";

	private int notificationIcon; // 通知 ICON
	private String notificationAppName; // 通知应用名称

	private String address; // address是接收人地址，仅在信息和邮件使用，否则可以不提供
	private String title; // title标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、 易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供
	private String titleUrl; // titleUrl是标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供
	private String text; // text是分享文本，所有平台都需要这个字段
	private String imageUrl; // imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段
	private String imagePath; // imagePath是本地的图片路径，除Linked-In外的所有平台都支持这个字段
	private String url; // url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供
	private String filePath; // filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
	private String comment; // comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
	private String site; // site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供
	private String siteUrl; // siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供
	private String venueName; // foursquare分享时的地方名
	private String venueDescription; // foursquare分享时的地方描述
	private float latitude = 1f; // 分享地纬度，新浪微博、腾讯微博和foursquare支持此字段
	private float longitude =1f; // 分享地经度，新浪微博、腾讯微博和foursquare支持此字段

	public int getNotificationIcon() {
		return notificationIcon;
	}

	public void setNotificationIcon(int notificationIcon) {
		this.notificationIcon = notificationIcon;
	}

	public String getNotificationAppName() {
		return notificationAppName;
	}

	public void setNotificationAppName(String notificationAppName) {
		this.notificationAppName = notificationAppName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleUrl() {
		return titleUrl;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public String getVenueDescription() {
		return venueDescription;
	}

	public void setVenueDescription(String venueDescription) {
		this.venueDescription = venueDescription;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

}
