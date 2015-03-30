package me.panavtec.cleancontacts.presentation.main;

import java.util.List;
import me.panavtec.cleancontacts.domain.entities.Contact;
import me.panavtec.cleancontacts.domain.interactors.base.Action;
import me.panavtec.cleancontacts.domain.interactors.base.InteractorInvoker;
import me.panavtec.cleancontacts.domain.interactors.base.InteractorOutput;
import me.panavtec.cleancontacts.domain.interactors.base.ThreadSpec;
import me.panavtec.cleancontacts.domain.interactors.contacts.GetContactsInteractor;
import me.panavtec.cleancontacts.domain.interactors.contacts.exceptions.RetrieveContactsException;
import me.panavtec.cleancontacts.presentation.Presenter;
import me.panavtec.cleancontacts.presentation.model.PresentationContact;
import me.panavtec.cleancontacts.presentation.model.mapper.base.ListMapper;

public class MainPresenter implements Presenter<MainView> {
  private final InteractorInvoker interactorInvoker;
  private final GetContactsInteractor getContactsInteractor;
  private final ListMapper<Contact, PresentationContact> listMapper;
  private final ThreadSpec mainThreadSpec;
  private final InteractorOutput<List<Contact>, RetrieveContactsException> getContactsListOutput;
  private MainView mainView;

  public MainPresenter(InteractorInvoker interactorInvoker,
      GetContactsInteractor getContactsInteractor,
      final ListMapper<Contact, PresentationContact> listMapper, ThreadSpec mainThreadSpec) {
    this.interactorInvoker = interactorInvoker;
    this.getContactsInteractor = getContactsInteractor;
    this.listMapper = listMapper;
    this.mainThreadSpec = mainThreadSpec;

    getContactsListOutput = new InteractorOutput.Builder<List<Contact>, RetrieveContactsException>(
        mainThreadSpec).onResult(new Action<List<Contact>>() {
      @Override public void onAction(List<Contact> data) {
        mainView.refreshContactsList(listMapper.modelToData(data));
      }
    }).onError(new Action<RetrieveContactsException>() {
      @Override public void onAction(RetrieveContactsException data) {
        mainView.showGetContactsError();
      }
    }).build();
  }

  @Override public void onCreate(MainView mainView) {
    this.mainView = mainView;
    this.mainView.initUi();
  }

  @Override public void onResume() {
    refreshContactList();
  }

  @Override public void onPause() {
  }

  @Override public void onDestroy() {
    this.mainView = null;
  }

  public void onRefresh() {
    mainView.refreshUi();
    refreshContactList();
  }

  private void refreshContactList() {
    interactorInvoker.execute(getContactsInteractor, getContactsListOutput);
  }
}
