package tuner.helpers.ModelController;

import tuner.helpers.ModelController.AnimaItem;

public enum HinaAnimaItem implements AnimaItem {
    CAFE_REACTION(0),
    Formation_Idle(1),
    Formation_Pickup(2),
    Callsign(3),
    MOVE_END(4),
    MOVING(5),
    ATTACK_IDLE(6),
    ATTACK_END(7),
    ATTACKING(8),
    ATTACK_START(9),
    RELOAD(11),
    Victory_End(12),
    Victory_Start(13),
    DYING(14),
    PANIC(15);

    @Override
    public int get() {
        return i;
    }
    private int i;

    HinaAnimaItem(int i){
        this.i=i;
    }

}
