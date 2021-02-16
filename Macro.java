package io.luxus.adofai;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.simple.parser.ParseException;

import io.luxus.api.adofai.ADOFAIMap;
import io.luxus.api.adofai.MapData;
import io.luxus.api.adofai.Tile;

public class Macro {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			Macro.program(scanner);
			System.out.println("계속하시려면 엔터키를 눌러주세요.");
			System.in.read();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			scanner.close();
		}
	}
	
	
	private static void program(Scanner scanner) throws IOException, ParseException, AWTException {
		System.out.println("A Dance of Fire and Ice 매크로");
		System.out.println("ver 1.0.0");
		System.out.println("개발자 : Luxus io");
		System.out.println("YouTube : https://www.youtube.com/channel/UCkznd9aLn0GXIP5VjDKo_nQ");
		System.out.println("Github : https://github.com/Luxusio");
		System.out.println();
		
		System.out.print("맵 경로 : ");
		
		String path = scanner.nextLine().trim();
		
		MapData mapData = new MapData();
		mapData.load(path);
		
		ADOFAIMap adofaiMap = new ADOFAIMap(mapData);
		
		System.out.print("추가 딜레이 : ");
		int loadDelay = scanner.nextInt();
		scanner.nextLine();
		
		List<Long> delayList = new ArrayList<>();
		BigDecimal result = bd("60000").divide(bd(mapData.getSetting().getBpm()), 100, RoundingMode.HALF_EVEN).multiply(bd("6.5")).add(bd(mapData.getSetting().getOffset())).add(bd(loadDelay)).multiply(bd("1000000"));
		long longResult = (long) result.longValue();
		delayList.add(longResult);
		BigDecimal error = result.subtract(bd(longResult));
		
		List<Tile> tileList = adofaiMap.getTileList();
		int size = tileList.size();
		
		for(int i=1;i<size;i++) {
			Tile tile = tileList.get(i);
			if(tile.getRelativeAngle() != 0) {
				double tempBPM = tile.getTempBPM();
				
				result = bd("60000.0").divide(bd(tempBPM), 100, RoundingMode.HALF_EVEN).multiply(bd("1000000"));
				
				
				longResult = (long) result.longValue();
				error.add(result.subtract(bd(longResult)));
				if(error.compareTo(bd("1000000.0")) == 1) {
					long longError = error.longValue();
					longResult -= longError;
					if(longResult < 1000000) {
						System.out.println("E: delay is smaller than 1ms");
						longResult = 1000000 - longResult;
						longError += longResult;
					}
					error = error.subtract(bd(longError));
				}
				
				delayList.add(longResult);
				
			}
		}
		
		Robot robot = new Robot();
		
		System.out.println();
		System.out.println("커서를 플레이버튼에 가져다주세요. 시작합니다.");
		
		
		for(int i=5;i>0;i--) {
			System.out.println(i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("start");
		
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		Iterator<Long> it = delayList.iterator();
		
		long prev_nanos = System.nanoTime();
		long curr_nanos;
		
		boolean mask = false;
		
		long delay = it.next();
		boolean keep = true;
		boolean nextKeep = it.hasNext();
		
		
		while (keep) {
			curr_nanos = System.nanoTime();
			if(curr_nanos - prev_nanos >= delay) {
				prev_nanos += delay;
				//System.out.println("c:" + delay / 1000000.0 + "ms");
				mask = !mask;
				if(mask) {
					robot.keyPress('J');
					robot.keyRelease('J');
				} else {
					robot.keyPress('F');
					robot.keyRelease('F');
				}
				if(nextKeep) {
					delay = it.next();
				}
				keep = nextKeep;
				nextKeep = it.hasNext();
			}
			
//			try {
//				Thread.sleep(1L);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		
		System.out.println("end");
		
		
	}
	
	public static BigDecimal bd(long value) {
		return bd(String.valueOf(value));
	}
	
	public static BigDecimal bd(double value) {
		return bd(String.valueOf(value));
	}
	
	public static BigDecimal bd(String str) {
		return new BigDecimal(str);
	}
	
}
