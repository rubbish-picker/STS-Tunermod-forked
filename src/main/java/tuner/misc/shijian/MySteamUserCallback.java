package tuner.misc.shijian;

import com.codedisaster.steamworks.*;

public class MySteamUserCallback implements SteamUserCallback {

    @Override
    public void onAuthSessionTicket(SteamAuthTicket steamAuthTicket, SteamResult steamResult) {}

    @Override
    public void onValidateAuthTicket(SteamID steamID, SteamAuth.AuthSessionResponse authSessionResponse, SteamID steamID1) {
//        long requesterID = steamID.getAccountID();
//        long ownerID = steamID1.getAccountID();
//
//        System.out.println("家庭共享用户 " + requesterID + " 正在使用所有者 " + ownerID + " 的游戏副本。");
    }

    @Override
    public void onMicroTxnAuthorization(int i, long l, boolean b) {}

    @Override
    public void onEncryptedAppTicket(SteamResult steamResult) {}
}
