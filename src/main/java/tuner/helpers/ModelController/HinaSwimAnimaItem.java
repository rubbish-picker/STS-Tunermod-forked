package tuner.helpers.ModelController;

public enum HinaSwimAnimaItem implements AnimaItem {
    CAFE_REACTION(0),
    Formation_Idle(1),
    Formation_Pickup(2),
    MOVE_END(3),
    MOVING(4),
    ATTACK_IDLE(5),
    ATTACK_END(6),
    ATTACKING(7),
    ATTACK_START(8),
    CALLSIGN(9),
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

    HinaSwimAnimaItem(int i){
        this.i=i;
    }

}
