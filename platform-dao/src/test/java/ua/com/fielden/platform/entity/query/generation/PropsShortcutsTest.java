package ua.com.fielden.platform.entity.query.generation;

import org.junit.Test;

import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.sample.domain.TgVehicle;
import static ua.com.fielden.platform.entity.query.fluent.query.select;

public class PropsShortcutsTest extends BaseEntQueryTCase {

    @Test
    public void test1() {
	assertModelsDifferent(//
		select(VEHICLE). //
		where().prop("model.id").eq().val(100).model(),

		select(VEHICLE). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		where().prop("model.id").eq().val(100).model());
    }

    @Test
    public void test_prop_to_source_association13() {
	assertModelsEquals(//
		select(VEHICLE). //
		where().prop("model.make.key").eq().val("MERC").model(),

		select(VEHICLE). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		where().prop("model.make.key").eq().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association14() {
	assertModelsEquals(//
		select(VEHICLE). //
		where().prop("station.key").like().val("AA%").and().prop("model.make.key").eq().val("MERC").model(),

		select(VEHICLE). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		leftJoin(ORG5).as("station").on().prop("station").eq().prop("station.id"). //
		where().prop("station.key").like().val("AA%").and().prop("model.make.key").eq().val("MERC").model());

    }

    @Test
    public void test_prop_to_source_association15() {
	final EntityResultQueryModel<TgVehicle> sourceQry = select(VEHICLE).as("v").where().prop("v.station.key").eq().val("AA").model();

	assertModelsEquals(//
		select(sourceQry). //
		where().prop("model.make.key").eq().val("MERC").model(),

		select(sourceQry). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		where().prop("model.make.key").eq().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association16() {
	final EntityResultQueryModel<TgVehicle> sourceQry = select(VEHICLE).as("v").where().prop("v.station.key").eq().val("AA").model();

	assertModelsEquals(//
		select(sourceQry). //
		leftJoin(ORG5).as("s").on().prop("station").eq().prop("s"). //
		where().prop("s.key").like().val("AA%").and().prop("model.make.key").eq().val("MERC").model(), //

		select(sourceQry). //
		leftJoin(ORG5).as("s").on().prop("station").eq().prop("s"). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		where().prop("s.key").like().val("AA%").and().prop("model.make.key").eq().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association17() {
	final EntityResultQueryModel<TgVehicle> sourceQry = select(VEHICLE).as("v").where().prop("v.station.key").eq().val("AA").model();

	assertModelsEquals(//
		select(sourceQry). //
		leftJoin(ORG5).as("s").on().prop("station").eq().prop("s"). //
		where().prop("s.parent.key").like().val("AA%").model(), //

		select(sourceQry). //
		leftJoin(ORG5).as("s").on().prop("station").eq().prop("s"). //
		leftJoin(ORG4).as("s.parent").on().prop("s.parent").eq().prop("s.parent.id"). //
		where().prop("s.parent.key").like().val("AA%").model());

    }

    @Test
    public void test_prop_to_source_association18() {
	final EntityResultQueryModel<TgVehicle> sourceQry = select(VEHICLE).as("v").where().prop("v.station.key").eq().val("AA").model();

	assertModelsEquals(//
		select(sourceQry). //
		leftJoin(ORG5).as("s").on().prop("station").eq().prop("s"). //
		where().prop("s.parent.key").like().val("AA%").and().prop("model.make.key").eq().val("MERC").model(), //

		select(sourceQry). //
		leftJoin(ORG5).as("s").on().prop("station").eq().prop("s"). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		leftJoin(ORG4).as("s.parent").on().prop("s.parent").eq().prop("s.parent.id"). //
		where().prop("s.parent.key").like().val("AA%").and().prop("model.make.key").eq().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association19() {
	assertModelsEquals(//
		select(VEHICLE). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		where().prop("model.make.key").eq().val("MERC").model(), //

		select(VEHICLE). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		where().prop("model.make.key").eq().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association20() {
	// demonstrate how user can misuse explicit joining and get incorrect result (used leftJoin instead of innerJoin) in case of vice-verse the result will be even worse - incomplete result set
	assertModelsEquals(//
		select(VEHICLE). //
		leftJoin(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		where().prop("model.make.key").eq().val("MERC").model(), //

		select(VEHICLE). //
		leftJoin(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		where().prop("model.make.key").eq().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association21() {
	assertModelsEquals(//
		select(VEHICLE). //
		leftJoin(VEHICLE).as("rv").on().prop("replacedBy").eq().prop("rv"). //
		where().prop("model.make.key").eq().val("MERC").model(), //

		select(VEHICLE). //
		leftJoin(VEHICLE).as("rv").on().prop("replacedBy").eq().prop("rv"). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		where().prop("model.make.key").eq().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association22() {
	assertModelsEquals(//
		select(VEHICLE). //
		leftJoin(VEHICLE).as("replacedBy").on().prop("replacedBy").eq().prop("replacedBy.id"). //
		where().prop("model.make.key").eq().val("MERC").model(), //

		select(VEHICLE). //
		leftJoin(VEHICLE).as("replacedBy").on().prop("replacedBy").eq().prop("replacedBy.id"). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		where().prop("model.make.key").eq().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association23() {
	assertModelsEquals(//
		select(VEHICLE). //
		leftJoin(VEHICLE).as("rv").on().prop("replacedBy").eq().prop("rv"). //
		where().prop("model.make.key").eq().val("MERC").and().prop("rv.model.make.key").ne().val("MERC").model(), //

		select(VEHICLE). //
		leftJoin(VEHICLE).as("rv").on().prop("replacedBy").eq().prop("rv"). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		leftJoin(MODEL).as("rv.model").on().prop("rv.model").eq().prop("rv.model.id"). //
		leftJoin(MAKE).as("rv.model.make").on().prop("rv.model.make").eq().prop("rv.model.make.id"). //
		where().prop("model.make.key").eq().val("MERC").and().prop("rv.model.make.key").ne().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association24() {
	// this illustrates the case of records being ate by explicit IJ after explicit LJ and how implicit joins are generated for (sub)properties of explicit IJ (aliased as rv2)
	assertModelsEquals(//
		select(VEHICLE). //
		leftJoin(VEHICLE).as("rv").on().prop("replacedBy").eq().prop("rv"). //
		join(VEHICLE).as("rv2").on().prop("rv.replacedBy").eq().prop("rv2"). //
		where().prop("model.make.key").eq().val("MERC").and().prop("rv2.model.make.key").ne().val("MERC").model(), //

		select(VEHICLE). //
		leftJoin(VEHICLE).as("rv").on().prop("replacedBy").eq().prop("rv"). //
		join(VEHICLE).as("rv2").on().prop("rv.replacedBy").eq().prop("rv2"). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		join(MODEL).as("rv2.model").on().prop("rv2.model").eq().prop("rv2.model.id"). //
		leftJoin(MAKE).as("rv2.model.make").on().prop("rv2.model.make").eq().prop("rv2.model.make.id"). //
		where().prop("model.make.key").eq().val("MERC").and().prop("rv2.model.make.key").ne().val("MERC").model());
    }

    @Test
    public void test_prop_to_source_association25_prev_but_with_explicit_id() {
	// this illustrates the case of records being ate by explicit IJ after explicit LJ and how implicit joins are generated for (sub)properties of explicit IJ (aliased as rv2)
	assertModelsEquals(//
		select(VEHICLE). //
		leftJoin(VEHICLE).as("rv").on().prop("replacedBy").eq().prop("rv.id"). //
		join(VEHICLE).as("rv2").on().prop("rv.replacedBy").eq().prop("rv2.id"). //
		where().prop("model.make.key").eq().val("MERC").and().prop("rv2.model.make.key").ne().val("MERC").model(), //

		select(VEHICLE). //
		leftJoin(VEHICLE).as("rv").on().prop("replacedBy").eq().prop("rv.id"). //
		join(VEHICLE).as("rv2").on().prop("rv.replacedBy").eq().prop("rv2.id"). //
		join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
		leftJoin(MAKE).as("model.make").on().prop("model.make").eq().prop("model.make.id"). //
		join(MODEL).as("rv2.model").on().prop("rv2.model").eq().prop("rv2.model.id"). //
		leftJoin(MAKE).as("rv2.model.make").on().prop("rv2.model.make").eq().prop("rv2.model.make.id"). //
		where().prop("model.make.key").eq().val("MERC").and().prop("rv2.model.make.key").ne().val("MERC").model());
    }
}