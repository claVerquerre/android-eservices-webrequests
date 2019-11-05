package android.eservices.webrequests.presentation.bookdisplay.search.adapter;

import java.util.List;

public interface BookActionInterface {

    void displayBooks(List<BookItemViewModel> bookItemViewModelList);

    void onFavoriteToggle(String bookId, boolean isFavorite);

    void onBookAddedToFavorites();

    void onBookRemovedFromFavorites();
}
