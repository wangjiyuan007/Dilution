package bean;

import lombok.Data;

/**
 * @Title: Rank
 * @Description: rank表实体类
 * @Company: www.leador.com.cn
 * @Date: 2019-07-23 10:26
 * @author: wangjiyuan
 * @Version: 1.0
 **/
@Data
public class Rank {
    private static final long serialVersionUID = 1L;

    private String CategoryName;
    private String Category;
    private String Type;
    private String Level;
    private String Brand;
    private String MinScaleID;
    private String MaxScaleID;
    private String Rank;
}
