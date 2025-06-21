package com.myapp.pizzahut;

import android.widget.SearchView;

import com.myapp.pizzahut.model.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchHelper {

    public static void setupSearchView(SearchView searchView, List<Product> pizzaList, List<Product> dessertList, List<Product> drinkList,
                                       PizzaAdapter pizzaAdapter, DessertsAdapter dessertsAdapter, DrinksAdapter drinksAdapter) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query, pizzaList, dessertList, drinkList, pizzaAdapter, dessertsAdapter, drinksAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText, pizzaList, dessertList, drinkList, pizzaAdapter, dessertsAdapter, drinksAdapter);
                return false;
            }
        });
    }

    private static void filterProducts(String query, List<Product> pizzaList, List<Product> dessertList, List<Product> drinkList,
                                       PizzaAdapter pizzaAdapter, DessertsAdapter dessertsAdapter, DrinksAdapter drinksAdapter) {
        List<Product> filteredPizzaList = new ArrayList<>();
        List<Product> filteredDessertList = new ArrayList<>();
        List<Product> filteredDrinkList = new ArrayList<>();

        for (Product product : pizzaList) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredPizzaList.add(product);
            }
        }

        for (Product product : dessertList) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredDessertList.add(product);
            }
        }

        for (Product product : drinkList) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredDrinkList.add(product);
            }
        }

       pizzaAdapter.updateList(filteredPizzaList);
        dessertsAdapter.updateList(filteredDessertList);
        drinksAdapter.updateList(filteredDrinkList);
    }
}
