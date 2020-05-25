package cn.travelround.core.bean.product;

import java.io.Serializable;

/**
 * Created by travelround on 2019/4/10.
 */
public class BrandQuery implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;//品牌名称
    private String description;//描述
    private String imgUrl;//图片网址
    private Integer sort;//排序 值越大越靠前
    private Integer isDisplay;//是否可用 0-不可用

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(Integer isDisplay) {
        this.isDisplay = isDisplay;
    }

    // 分页添加的字段
    private Integer pageNo = 1;// 页码
    private Integer pageSize = 10;// 一页显示几条数据
    private Integer startRow;// 开始行

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.startRow = (pageNo - 1)*pageSize;
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.startRow = (pageNo - 1)*pageSize;
        this.pageSize = pageSize;
    }

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    @Override
    public String toString() {
        return "BrandQuery{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", sort=" + sort +
                ", isDisplay=" + isDisplay +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", startRow=" + startRow +
                '}';
    }
}
