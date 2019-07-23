package edu.cnm.deepdive.qodclient.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.cnm.deepdive.qodclient.R;
import edu.cnm.deepdive.qodclient.databinding.ActivityMainBinding;
import edu.cnm.deepdive.qodclient.model.Quote;
import edu.cnm.deepdive.qodclient.service.GoogleSignInService;
import edu.cnm.deepdive.qodclient.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

  private ActivityMainBinding binding;
  private MainViewModel viewModel;
  private boolean randomIgnored = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupViewModel();
    setupBinding();
    setupObservers();
    setupToolbar();
    setupSearch();
    setupFab();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.sign_out:
        signOut();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
    Quote quote = (Quote) adapterView.getItemAtPosition(position);
    String term = binding.content.searchTerm.getText().toString().trim();
    String title = term.isEmpty() ? getString(R.string.search_all)
        : getString(R.string.search_title_format, term);
    Runnable next = (position >= adapterView.getCount() - 1) ? null : () -> {
      if (position < adapterView.getCount() - 1) {
        binding.content.searchResults.performItemClick(null, position + 1,
            binding.content.searchResults.getItemIdAtPosition(position + 1));
      }
    };
    showQuote(quote, title, next);
  }

  private void setupViewModel() {
    viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    getLifecycle().addObserver(viewModel);
  }

  private void setupBinding() {
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.setViewModel(viewModel);
  }

  private void setupObservers() {
    viewModel.randomQuote().observe(this, (quote) -> {
      if (!randomIgnored) {
        showQuote(quote, getString(R.string.random_quote_title), () -> viewModel.nextRandomQuote());
      }
    });
    viewModel.searchResults().observe(this, (quotes) -> {
      ArrayAdapter<Quote> adapter =
          new ArrayAdapter<>(this, R.layout.quote_list_item, quotes);
      binding.content.searchResults.setAdapter(adapter);
    });
  }

  private void setupSearch() {
    binding.content.search.setOnClickListener((v) -> viewModel.search());
    binding.content.clear.setOnClickListener((v) -> {
      binding.content.searchTerm.getText().clear();
      viewModel.setSearchTerm(null);
      viewModel.search();
    });
    binding.content.searchResults.setOnItemClickListener(this);
  }

  private void setupToolbar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  private void setupFab() {
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(view -> {
      randomIgnored = false;
      viewModel.nextRandomQuote();
    });
    TooltipCompat.setTooltipText(fab, getString(R.string.random_quote_tooltip));
  }

  private void showQuote(Quote quote, String title, Runnable nextAction) {
    AlertDialog.Builder builder = new Builder(this)
        .setMessage(quote.getCombinedText(getString(R.string.combined_quote_pattern),
            getString(R.string.source_delimiter), getString(R.string.unknown_source)))
        .setTitle(title)
        .setNegativeButton(R.string.dialog_done, (dialogInterface, i) -> {});
    if (nextAction != null) {
      builder.setPositiveButton(R.string.dialog_next, (dialogInterface, i) -> nextAction.run());
    }
    builder.create().show();
  }

  private void signOut() {
    GoogleSignInService service = GoogleSignInService.getInstance();
    service.getClient().signOut().addOnCompleteListener((task) -> {
      service.setAccount(null);
      Intent intent = new Intent(this, LoginActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    });
  }

}
