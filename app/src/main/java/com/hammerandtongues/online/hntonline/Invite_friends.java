package com.hammerandtongues.online.hntonline;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

public class Invite_friends extends Activity {

    private TextView resultText;
    private SearchView mysearchfield;
    private LinearLayout contactsscroll;
    RelativeLayout main;
    private static final int REQUEST_INVITE = 2;

    private static final
    String INVITATION_TITLE = "Invite friends to HnTOnline",
            INVITATION_MESSAGE = "Hey! Download HnTShopping App and enjoy convinience! :)",
            INVITATION_CALL_TO_ACTION = "Invite";
    private Button invite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_invite_friends);

invite = (Button) findViewById(R.id.btninvite);

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new AppInviteInvitation.IntentBuilder(INVITATION_TITLE)
                        .setMessage(INVITATION_MESSAGE)
                        .setDeepLink(Uri.parse("https://devshop.hammerandtongues.com/"))
                        .setCallToActionText(INVITATION_CALL_TO_ACTION)
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {

                // You successfully sent the invite,
                // we can dismiss the button.
                //button.setVisibility(View.GONE);

                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                StringBuilder sb = new StringBuilder();
                sb.append("Sent ").append(Integer.toString(ids.length)).append(" invitations: ");
                for (String id : ids) sb.append("[").append(id).append("]");
                Log.d(getString(R.string.app_name), sb.toString());

            } else {
                // Sending failed or it was canceled using the back button
               //showMessage("Sorry, I wasn't able to send the invites");
                Toast ToastMessage = Toast.makeText(Invite_friends.this,"Invite not sent!",Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();
            }
        }
    }


}