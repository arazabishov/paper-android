package io.paper.android.listnotes;

import android.support.annotation.NonNull;

import io.paper.android.commons.schedulers.SchedulerProvider;
import io.paper.android.commons.views.View;
import io.paper.android.notes.NotesRepository;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

class ListNotesPresenterImpl implements ListNotesPresenter {
    private final SchedulerProvider schedulerProvider;
    private final NotesRepository notesRepository;
    private final CompositeDisposable disposable;

    ListNotesPresenterImpl(SchedulerProvider schedulerProvider,
            NotesRepository notesRepository) {
        this.schedulerProvider = schedulerProvider;
        this.notesRepository = notesRepository;
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void attachView(@NonNull View view) {
        if (view instanceof ListNotesView) {
            ListNotesView listNotesView = (ListNotesView) view;

            disposable.add(notesRepository.list()
                    .observeOn(schedulerProvider.ui())
                    .subscribe(listNotesView.showNotes()));

            disposable.add(listNotesView.createNoteButtonClicks()
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.io())
                    .switchMap((event) -> notesRepository.add("", ""))
                    .subscribe(listNotesView.navigateToEditNoteView(), Timber::e));
        }
    }

    @Override
    public void detachView() {
        disposable.clear();
    }
}