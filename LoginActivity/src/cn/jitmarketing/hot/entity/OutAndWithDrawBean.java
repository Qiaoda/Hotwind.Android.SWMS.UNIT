package cn.jitmarketing.hot.entity;

import java.io.Serializable;

/**
 * 鞋子出样另一只、撤样列表实体
 *
 * @author chuwe1
 */
public class OutAndWithDrawBean implements Serializable{

    // 公共字段
    private String MappingID;
    private String ShelfLocationCode;
    private String UserCode;
    private String LastUpdateTime;

    // 撤样独有字段
    private String SKUID;
    private float Count;

    public String getMappingID() {
        return MappingID;
    }

    public void setMappingID(String mappingID) {
        MappingID = mappingID;
    }

    public String getShelfLocationCode() {
        return ShelfLocationCode;
    }

    public void setShelfLocationCode(String shelfLocationCode) {
        ShelfLocationCode = shelfLocationCode;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    public String getSKUID() {
        return SKUID;
    }

    public void setSKUID(String SKUID) {
        this.SKUID = SKUID;
    }

    public float getCount() {
        return Count;
    }

    public void setCount(float count) {
        Count = count;
    }
}
