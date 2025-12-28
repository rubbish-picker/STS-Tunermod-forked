package tuner.helpers.ModelController;

public enum HinaDressAnimaItem implements AnimaItem {
    CAFE_REACTION(0),
    Formation_Idle(1),
    Formation_Idle_Random(2),
    Formation_Pickup(3),
    MOVE_END(4),
    MOVING(5),
    ATTACK_IDLE(6),
    ATTACK_END(7),
    ATTACKING(8),
    ATTACK_START(9),
    Callsign(10),
    Callsign_Random(11),
    RELOAD(13),
    Reload_Random(14),
    Victory_End(15),
    Victory_End_Random(16),
    Victory_Start(17),
    Victory_Start_Random(18),
    DYING(19),
    PANIC(20);

    @Override
    public int get() {
        return i;
    }
    private int i;

    HinaDressAnimaItem(int i){
        this.i=i;
    }

}
