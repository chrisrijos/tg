package ua.com.fielden.platform.entity.query.model.transformation;

import java.util.SortedMap;
import java.util.TreeMap;

import ua.com.fielden.platform.entity.query.model.structure.QueryModelResult.ResultPropertyInfo;

public class SqlEntQuery implements IQuerySource {

    private final SortedMap<String, IYieldedItem> yields = new TreeMap<String, IYieldedItem>();



    @Override
    public IQuerySourceItem getSourceItem(final String dotNotatedName) {
	return yields.get(dotNotatedName);
    }

    @Override
    public boolean hasReferences() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public ResultPropertyInfo getPropInfo(final String dotNotatedPropName) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getSql() {
	// TODO Auto-generated method stub
	return null;
    }

}
