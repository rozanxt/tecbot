package zan.tecbot.object.block;

import zan.tecbot.mechanism.GridMap;

public class SwitchBlock extends Block {
	
	protected GridMap gridMap;
	
	protected boolean switchAble;
	protected int switchTimer;
	protected int switchCount;
	
	public SwitchBlock(int sx, int sy, GridMap gm) {
		super(sx, sy);
		gridMap = gm;
		powered = false;
		switchAble = false;
		switchTimer = 0;
		switchCount = 0;
	}
	
	public void setSwitchAble(boolean ss) {switchAble = ss;}
	public boolean isSwitchAble() {return switchAble;}
	
	public void setSwitchTimer(int st) {switchTimer = st;}
	
	public void switchPower() {
		if (isSwitchAble()) {
			if (isPowered()) setPowered(false);
			else setPowered(true);
		} else setPowered(true);
		if (switchTimer > 0 && isPowered()) switchCount = switchTimer;
		gridMap.checkWire(getWireID());
	}
	
	public void update() {
		super.update();
		if (switchTimer > 0 && isPowered()) {
			if (switchCount > 0) switchCount--;
			else setPowered(false);
		}
	}
	
}
