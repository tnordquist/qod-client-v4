package edu.cnm.deepdive.qodclient.controller;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import edu.cnm.deepdive.qodclient.R;
import edu.cnm.deepdive.qodclient.model.Quote;
import edu.cnm.deepdive.qodclient.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

  private MainViewModel viewModel;
  private ListView searchResults;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupToolbar();
    setupSearch();
    setupFab();
    setupViewModel();
  }

  private void setupSearch() {
    EditText searchTerm = findViewById(R.id.search_term);
    ImageButton search = findViewById(R.id.search);
    ImageButton clear = findViewById(R.id.clear);
    searchResults = findViewById(R.id.search_results);
    search.setOnClickListener((v) -> viewModel.search(searchTerm.getText().toString().trim()));
    clear.setOnClickListener((v) -> {
      searchTerm.getText().clear();
      viewModel.search(null);
    });
    searchResults.setOnItemClickListener((adapterView, view, position, rowId) -> {
      Quote quote = (Quote) adapterView.getItemAtPosition(position);
      String term = searchTerm.getText().toString().trim();
      String title = term.isEmpty() ? getString(R.string.search_all)
          : getString(R.string.search_title_format, term);
      showQuote(quote, title, () -> {
        if (position < adapterView.getCount() - 1) {
          searchResults.performItemClick(null, position + 1,
              searchResults.getItemIdAtPosition(position + 1));
        }
      });
    });
  }

  private void setupToolbar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  private void setupFab() {
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(view -> viewModel.getRandomQuote());
    TooltipCompat.setTooltipText(fab, getString(R.string.random_quote_tooltip));
  }

  private void setupViewModel() {
    viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    getLifecycle().addObserver(viewModel);
    viewModel.getRandomQuote().observe(this, (quote) ->
        showQuote(quote, getString(R.string.random_quote_title), () -> viewModel.getRandomQuote()));
    viewModel.search(null).observe(this, (quotes) -> {
      ArrayAdapter<Quote> adapter =
          new ArrayAdapter<>(this, R.layout.quote_list_item, quotes);
      searchResults.setAdapter(adapter);
    });
  }

  private void showQuote(Quote quote, String title, Runnable nextAction) {
    new Builder(this)
        .setMessage(quote.getCombinedText(getString(R.string.combined_quote_pattern),
            getString(R.string.source_delimiter), getString(R.string.unknown_source)))
        .setTitle(title)
        .setPositiveButton(R.string.dialog_next, (dialogInterface, i) -> nextAction.run())
        .setNegativeButton(R.string.dialog_done, (dialogInterface, i) -> {})
        .create()
        .show();
  }

}
