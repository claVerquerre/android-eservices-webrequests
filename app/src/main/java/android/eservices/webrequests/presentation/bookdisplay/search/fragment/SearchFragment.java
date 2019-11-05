package android.eservices.webrequests.presentation.bookdisplay.search.fragment;

import android.eservices.webrequests.R;
import android.eservices.webrequests.presentation.bookdisplay.search.BookSearchContract;
import android.eservices.webrequests.presentation.bookdisplay.search.adapter.BookActionInterface;
import android.eservices.webrequests.presentation.bookdisplay.search.adapter.BookAdapter;
import android.eservices.webrequests.presentation.bookdisplay.search.adapter.BookItemViewModel;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/*
 * uncheck favorite selection in search results when favorite unchecked from Favorite fragment
 */
public class SearchFragment extends Fragment implements BookActionInterface {

    public static final String TAB_NAME = "Search";
    private View rootView;
    BookSearchContract.Presenter bookSearchPresenter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private ProgressBar progressBar;

    private SearchFragment() {
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupSearchView();
        setupRecyclerView();
        progressBar = rootView.findViewById(R.id.progress_bar);

    }

    private void setupSearchView() {
        searchView = rootView.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private Timer timer = new Timer();

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                if (s.length() == 0) {
                    bookSearchPresenter.cancelSubscription();
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    timer.cancel();
                    timer = new Timer();
                    int sleep = 350;
                    if (s.length() == 1)
                        sleep = 5000;
                    else if (s.length() <= 3)
                        sleep = 300;
                    else if (s.length() <= 5)
                        sleep = 200;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            bookSearchPresenter.searchBooks(s);
                        }
                    }, sleep);
                }
                return true;
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        bookAdapter = new BookAdapter(this);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void displayBooks(List<BookItemViewModel> bookItemViewModelList) {
        progressBar.setVisibility(View.GONE);
        bookAdapter.bindViewModels(bookItemViewModelList);
    }

    @Override
    public void onFavoriteToggle(String bookId, boolean isFavorite) {
        if (isFavorite) {
            bookSearchPresenter.addBookToFavorite(bookId);
        } else {
            bookSearchPresenter.removeBookFromFavorites(bookId);
        }
    }

    @Override
    public void onBookAddedToFavorites() {
        //Do nothing
    }

    @Override
    public void onBookRemovedFromFavorites() {
        //Do nothing
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bookSearchPresenter.detachView();
    }
}
