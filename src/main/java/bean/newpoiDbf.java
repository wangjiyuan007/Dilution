package bean;

import lombok.Data;

/**
 * @Title: newpoiDbf
 * @Description: 新生成的poi的属性实体类
 * @Company: www.leador.com.cn
 * @Date: 2019-07-23 21:46
 * @author: wangjiyuan
 * @Version: 1.0
 **/
@Data
public class newpoiDbf {
    private static final long serialVersionUID = 1L;

    private String POIID;
    private String GUIID;
    private String Name_chn;
    private String Name_trd;
    private String Name_eng;
    private String X_coord;
    private String Y_coord;
    private String Type;
    private String Level;
    private String Brand;
    private String priority;
    private String CategoryNa;
    private String Category;
    private String MinScaleID;
    private String MaxScaleID;
    private String Rank;
    private String Delete;
}
