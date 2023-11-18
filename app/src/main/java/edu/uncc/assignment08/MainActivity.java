package edu.uncc.assignment08;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener,
        RegisterFragment.RegisterListener, ContactsFragment.ContactsListener, CreateContactFragment.CreateContactListener,
        AddPhoneNumberFragment.AddPhoneNumberListener, ContactDetailsFragment.ContactDetailsListener,
        EditContactFragment.EditContactListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.rootView, new LoginFragment())
                    .commit();
        }else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.rootView, new ContactsFragment())
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    public void authSuccessful() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new ContactsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void login() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void register() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new RegisterFragment())
                .commit();
    }

    @Override
    public void createContact() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new CreateContactFragment(), "create-contact-fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void contactDetails(Contact contact) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ContactDetailsFragment.newInstance(contact))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void addPhoneNumber() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new AddPhoneNumberFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void backToContactsFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void addPhoneNumberToContact(PhoneNumber phoneNumber) {
        CreateContactFragment createContactFragment = (CreateContactFragment)
                getSupportFragmentManager().findFragmentByTag("create-contact-fragment");

        EditContactFragment editContactFragment = (EditContactFragment)
                getSupportFragmentManager().findFragmentByTag("edit-contact");

        if (createContactFragment != null){
            createContactFragment.addPhoneNumbers(phoneNumber);
        }

        if (editContactFragment != null){
            editContactFragment.addPhoneNumber(phoneNumber);
        }

        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goBackToCreateContact() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goBack() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void editContact(Contact contact) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, EditContactFragment.newInstance(contact), "edit-contact")
                .addToBackStack(null)
                .commit();
    }
}