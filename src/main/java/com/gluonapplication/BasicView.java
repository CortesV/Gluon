package com.gluonapplication;

import com.gluonapplication.components.entity.Density;
import com.gluonapplication.components.entity.Density.DENSITY;
import com.gluonapplication.components.entity.USState;
import com.gluonapplication.components.entity.USStates;
import com.gluonhq.charm.glisten.control.*;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class BasicView extends View {

	private final FilteredList<USState> filteredList;
	private final CharmListView<USState, DENSITY> charmListView;
	private boolean ascending = true;

	public BasicView(String name) {
		super(name);

		filteredList = new FilteredList<>(USStates.statesList, getStatePredicate(null));
		charmListView = new CharmListView<>(filteredList);
		charmListView.setCellFactory(p -> new USStateCell());
		charmListView.setHeadersFunction(Density::getDensity);
		charmListView.setHeaderCellFactory(p -> new CharmListCell<USState>() {

			private final ListTile tile = new ListTile();

			{
				Avatar avatar = new Avatar(16, USStates.getUSFlag());
				tile.setPrimaryGraphic(avatar);
				setText(null);
			}

			@Override
			public void updateItem(USState item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null && !empty) {
					tile.textProperty().setAll("Density", charmListView.toString(item));
					setGraphic(tile);
				} else {
					setGraphic(null);
				}
			}

		});
		charmListView.setConverter(new StringConverter<DENSITY>() {

			@Override
			public String toString(DENSITY d) {
				return "From " + ((int) d.getIni()) + " up to " + ((int) d.getEnd()) + " pop/km" + "\u00B2";
			}

			@Override
			public DENSITY fromString(String string) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});

		setCenter(charmListView);

	}

	@Override
	protected void updateAppBar(AppBar appBar) {
		appBar.setNavIcon(MaterialDesignIcon.STAR.button());
		appBar.setTitleText("50 States");

		Button sort = MaterialDesignIcon.SORT.button(e -> {
			if (ascending) {
				charmListView.setHeaderComparator((d1, d2) -> d1.compareTo(d2));
				charmListView.setComparator((s1, s2) -> Double.compare(s1.getDensity(), s2.getDensity()));
				ascending = false;
			} else {
				charmListView.setHeaderComparator((d1, d2) -> d2.compareTo(d1));
				charmListView.setComparator((s1, s2) -> Double.compare(s2.getDensity(), s1.getDensity()));
				ascending = true;
			}
		});
		appBar.getActionItems().add(sort);
		appBar.getMenuItems().setAll(buildFilterMenu());

	}

	private Predicate<USState> getStatePredicate(Double population) {
		return state -> population == null || state.getPopulation() >= population * 1_000_000;
	}

	private List<MenuItem> buildFilterMenu() {
		final List<MenuItem> menu = new ArrayList<>();

		EventHandler<ActionEvent> menuActionHandler = e -> {
			MenuItem item = (MenuItem) e.getSource();
			Double population = (Double) item.getUserData();
			filteredList.setPredicate(getStatePredicate(population));
		};

		ToggleGroup toggleGroup = new ToggleGroup();

		RadioMenuItem allStates = new RadioMenuItem("All States");
		allStates.setOnAction(menuActionHandler);
		allStates.setSelected(true);
		menu.add(allStates);
		toggleGroup.getToggles().add(allStates);

		List<Double> items = Arrays.asList(0.5, 1.0, 2.5, 5.0);
		for (Double d : items) {
			RadioMenuItem item = new RadioMenuItem("Population > " + d + "M");
			item.setUserData(d);
			item.setOnAction(menuActionHandler);
			menu.add(item);
			toggleGroup.getToggles().add(item);
		}

		return menu;
	}

}
