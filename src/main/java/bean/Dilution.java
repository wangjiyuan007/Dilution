package bean;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

/**
 * @Title: Dilution
 * @Description: 抽稀的操作对象
 * @Company: www.leador.com.cn
 * @Date: 2019-07-24 15:09
 * @author: wangjiyuan
 * @Version: 1.0
 **/
@Data
public class Dilution {
    private static final long serialVersionUID = 1L;

    private Geometry the_geom;
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
    private Long priority;
    private String CategoryNa;
    private String Category;
    private String MinScaleID;
    private String MaxScaleID;
    private String Rank;
    private String Delete;
}
