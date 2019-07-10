package edu.cnm.deepdive.qodclient.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import edu.cnm.deepdive.qodclient.model.Quote;
import edu.cnm.deepdive.qodclient.service.QodService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.LinkedList;
import java.util.List;

public class MainViewModel extends AndroidViewModel implements LifecycleObserver {

  private MutableLiveData<Quote> random;
  private MutableLiveData<List<Quote>> searchResults;
  private CompositeDisposable pending = new CompositeDisposable();

  public MainViewModel(@NonNull Application application) {
    super(application);
  }

  public LiveData<Quote> randomQuote() {
    if (random == null) {
      random = new MutableLiveData<>();
    }
    return random;
  }

  public void getRandomQuote() {
    pending.add(
        QodService.getInstance().random()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((quote) -> random.setValue(quote))
    );
  }

  public LiveData<List<Quote>> searchResults() {
    if (searchResults == null) {
      searchResults = new MutableLiveData<>();
    }
    return searchResults;
  }

  public void search(String term) {
    if (term != null) {
      pending.add(
          QodService.getInstance().search(term)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe((quotes) -> searchResults.setValue(quotes))
      );
    } else {
      searchResults.setValue(new LinkedList<>());
    }
  }

  @OnLifecycleEvent(Event.ON_STOP)
  public void disposePending() {
    pending.clear();
  }

}
