package emailClient.service;

import emailClient.model.EmailAccount;
import emailClient.model.EmailFolder;
import emailClient.model.EmailMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

public class EmailManager {

    private final ObservableList<EmailAccount> emailAccounts = FXCollections.observableArrayList();

    private final EmailFolder foldersRoot = new EmailFolder("");

    private final List<Folder> folderList = new ArrayList<>();

    private EmailMessage selectedMessage;

    private EmailFolder selectedFolder;

    private boolean rememberCredentials;

    private final IconsHandler iconsHandler;

    public EmailManager() {
        FolderUpdaterService folderUpdaterService = new FolderUpdaterService(folderList);
        folderUpdaterService.start();
        this.iconsHandler = new IconsHandler();
    }

    public EmailFolder getFoldersRoot() {
        return foldersRoot;
    }

    public EmailMessage getSelectedMessage() {
        return selectedMessage;
    }

    public void setSelectedMessage(EmailMessage selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    public void setSelectedFolder(EmailFolder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public void addEmailAccount(EmailAccount emailAccount) {
        emailAccounts.add(emailAccount);
        EmailFolder emailFolder = new EmailFolder(emailAccount.getEmailAddress());
        emailFolder.setGraphic(iconsHandler.getIconForFolder(emailAccount.getEmailAddress()));
        FetchFolderService fetchFolderService = new FetchFolderService(emailAccount.getStore(), emailFolder, folderList);
        fetchFolderService.start();
        foldersRoot.getChildren().add(emailFolder);
    }

    public ObservableList<EmailAccount> getEmailAccounts() {
        return emailAccounts;
    }

    public void setRead() {
        try {
            selectedMessage.setRead(true);
            selectedMessage.getMessage().setFlag(Flags.Flag.SEEN, true);
            selectedFolder.decrementUnreadMessagesCount();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void setUnread() {
        try {
            selectedMessage.setRead(false);
            selectedMessage.getMessage().setFlag(Flags.Flag.SEEN, false);
            selectedFolder.incrementUnreadMessagesCount();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void deleteSelectedMessage() {
        try {
            selectedMessage.getMessage().setFlag(Flags.Flag.DELETED, true);
            selectedFolder.getMessages().remove(selectedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRememberCredentials() {
        return rememberCredentials;
    }

    public void setRememberCredentials(boolean rememberCredentials) {
        this.rememberCredentials = rememberCredentials;
    }
}
