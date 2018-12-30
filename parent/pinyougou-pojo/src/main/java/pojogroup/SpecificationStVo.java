package pojogroup;

import cn.itcast.core.pojo.specification.SpecificationOptionSt;
import cn.itcast.core.pojo.specification.SpecificationSt;

import java.io.Serializable;
import java.util.List;

public class SpecificationStVo implements Serializable{
    private SpecificationSt specificationSt;
    private List<SpecificationOptionSt>  specificationOptionStList;

    public SpecificationSt getSpecificationSt() {
        return specificationSt;
    }

    public void setSpecificationSt(SpecificationSt specificationSt) {
        this.specificationSt = specificationSt;
    }

    public List<SpecificationOptionSt> getSpecificationOptionStList() {
        return specificationOptionStList;
    }

    public void setSpecificationOptionStList(List<SpecificationOptionSt> specificationOptionStList) {
        this.specificationOptionStList = specificationOptionStList;
    }

    @Override
    public String toString() {
        return "SpecificationStVo{" +
                "specificationSt=" + specificationSt +
                ", specificationOptionStList=" + specificationOptionStList +
                '}';
    }
}
