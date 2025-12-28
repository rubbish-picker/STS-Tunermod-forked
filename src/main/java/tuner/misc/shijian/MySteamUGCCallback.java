package tuner.misc.shijian;

import com.codedisaster.steamworks.*;
import tuner.misc.SimpleHttpClient;
import tuner.patches.utils.MetricsPatch;

public class MySteamUGCCallback implements SteamUGCCallback {
    @Override
    public void onGetUserItemVote(SteamPublishedFileID fileID,
                                  boolean voteUp,
                                  boolean voteDown,
                                  boolean voted,
                                  SteamResult result) {
        if (voteUp) {
            SimpleHttpClient.sendMessage(Shijian.steamUserName,
                    i->{
                        Shijian.giveMe( (int) Math.round(i), 0);
                    },
                    Shijian.gatherAllData(MetricsPatch.death, MetricsPatch.trueVictor, MetricsPatch.monsters, 0));
        } else if(voteDown) {
            SimpleHttpClient.sendMessage(Shijian.steamUserName,
                    i->{
                        Shijian.giveMe( (int) Math.round(i), 1);
                    },
                    Shijian.gatherAllData(MetricsPatch.death, MetricsPatch.trueVictor, MetricsPatch.monsters, 1));
        } else{
            SimpleHttpClient.sendMessage(Shijian.steamUserName,
                    i->{
                        Shijian.giveMe( (int) Math.round(i), 2);
                    },
                    Shijian.gatherAllData(MetricsPatch.death, MetricsPatch.trueVictor, MetricsPatch.monsters, 2));
        }
    }

    @Override
    public void onSetUserItemVote(SteamPublishedFileID steamPublishedFileID, boolean b, SteamResult steamResult) {
    }

    @Override
    public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned,
                                    int totalMatchingResults, boolean isCachedData,SteamResult steamResult) {
    }

    public void onSubscribeItem(SteamPublishedFileID steamPublishedFileID, SteamResult steamResult) {}

    public void onUnsubscribeItem(SteamPublishedFileID steamPublishedFileID, SteamResult steamResult) {}

    public void onRequestUGCDetails(SteamUGCDetails steamUGCDetails, SteamResult steamResult) {
        System.out.println("steamUgc!!!");
    }

    public void onCreateItem(SteamPublishedFileID steamPublishedFileID, boolean b, SteamResult steamResult) {}

    public void onSubmitItemUpdate(SteamPublishedFileID steamPublishedFileID, boolean b, SteamResult steamResult) {}

    public void onDownloadItemResult(int i, SteamPublishedFileID steamPublishedFileID, SteamResult steamResult) {}

    public void onUserFavoriteItemsListChanged(SteamPublishedFileID steamPublishedFileID, boolean b, SteamResult steamResult) {}

    public void onStartPlaytimeTracking(SteamResult steamResult) {}

    public void onStopPlaytimeTracking(SteamResult steamResult) {}

    public void onStopPlaytimeTrackingForAllItems(SteamResult steamResult) {}

    public void onDeleteItem(SteamPublishedFileID steamPublishedFileID, SteamResult steamResult) {}
}
