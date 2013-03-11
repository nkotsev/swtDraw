package org.elsys.swt.Drawing;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Drawing {

	private DrawingPoint start;
	private DrawingPoint end;
	private DrawingPoint movePoint;

	public static void main(String[] args) {
		Drawing d = new Drawing();
		d.createCanvas();
	}

	public void createCanvas() {
		final List<DrawingPoint> points = new ArrayList<DrawingPoint>();
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setLayout(new FillLayout());
		Shell shell2 = new Shell(d);
		shell2.setSize(300, 100);
		final Text text = new Text(shell2, SWT.BORDER);
		shell2.setLayout(new FillLayout());

		Button compute = new Button(shell2, SWT.PUSH);
		compute.setText("Compute");
		
		shell2.open();
		final Canvas canvas = new Canvas(shell, SWT.BORDER);
		canvas.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		

		
		compute.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					String func[] = text.getText().split(":");
					if(func.length!=2) {
						text.setText("Wrong Format.");
					}else if(func[0].equals("p")) {
						String arr[] = func[1].split(",");
						if(arr.length!=3) { 
							text.setText("Needs 3 elements."); 
						} else {
							text.setText("p:" + Integer.toString(calcDistance(points, arr)));
						}	
					}else if(func[0].equals("a")){
						String arr[] = func[1].split(",");
						if(arr.length!=3) { 
							text.setText("Needs 3 elements."); 
						} else {
							text.setText("a:" + Double.toString(calcArea(points, arr)));
						}
					}else if(func[0].equals("angle")){
						String arr[] = func[1].split(",");
						if(arr.length!=3) { 
							text.setText("Needs 3 elements."); 
						} else {
							text.setText("angle:" + Double.toString(calcAngle(points, arr)));
						}
					}else if(func[0].equals("angles")){
						String arr[] = func[1].split(",");
						if(arr.length != 3){
							text.setText("Needs 3 elements");
						}else{
							text.setText("angles:" + calcAngles(points, arr));
						}
					}						
				}
			}

		

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			} 
		});

		canvas.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				end = new DrawingPoint(e.x, e.y);
				canvas.redraw();
			}

		});
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					start = new DrawingPoint(e.x, e.y);
				} else if (e.button == 3) {
					movePoint = findPoint(points, e.x, e.y);
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {
					points.add(start);
					start.title = Integer.toString(points.size());
					// points.add(new Point(e.x, e.y));
					// start = null;
					canvas.redraw();
				} else if (e.button == 3) {
					movePoint = null;
				}
			}

		});
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				drawLines(points, e.gc);
			}
		});
		shell.open();

		while (!shell.isDisposed()) {
			if (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

	private DrawingPoint findPoint(List<DrawingPoint> points, int x, int y) {
		for (DrawingPoint p : points) {
			if (Math.abs(p.x - x) < 5 && Math.abs(p.y - y) < 5)
				return p;
		}
		return null;
	}
	private int calcDistance(final List<DrawingPoint> points, String array[]) {
		boolean found;
		List<DrawingPoint> worklist = new ArrayList<DrawingPoint>();
		int temp=0;
		for(int t=0; t<array.length; t++) {
			found=false;
			for(int i=0; i<points.size(); i++) {
				if(points.get(i).title.equals(array[t])) {
					worklist.add(points.get(i));
					found=true;
					break;
				}
			}
			if(found==false) {
				return -1;
			}	
		}
		for (int i = 0; i < worklist.size(); i++) {
			for (int k = i + 1; k < worklist.size(); k++) {
			temp+=calcLine(worklist.get(i),worklist.get(k));	
			}
		}
		return temp;
	}
	private double calcArea(List<DrawingPoint> points, String array[]){
		int a,b,c;
		int p = calcDistance(points, array)/2;
		boolean found;
		List<DrawingPoint> worklist = new ArrayList<DrawingPoint>();
		int temp=0;
		for(int t=0; t<array.length; t++) {
			found=false;
			for(int i=0; i<points.size(); i++) {
				if(points.get(i).title.equals(array[t])) {
					worklist.add(points.get(i));
					found=true;
					break;
				}
			}
			if(found==false) {
				return -1;
			}	
		}
		a = calcLine(worklist.get(0),worklist.get(1));
		b = calcLine(worklist.get(1),worklist.get(2));
		c = calcLine(worklist.get(0),worklist.get(2));
		double s = Math.sqrt((double)(p*(p-a)*(p-b)*(p-c)));
		return s;
	}
	private double calcAngle(List<DrawingPoint> points, String array[]){
		double a,b,c;
		int p = calcDistance(points, array)/2;
		boolean found;
		List<DrawingPoint> worklist = new ArrayList<DrawingPoint>();
		int temp=0;
		for(int t=0; t<array.length; t++) {
			found=false;
			for(int i=0; i<points.size(); i++) {
				if(points.get(i).title.equals(array[t])) {
					worklist.add(points.get(i));
					found=true;
					break;
				}
			}
			if(found==false) {
				return -1;
			}	
		}
		
	
		a = calcLine(worklist.get(0),worklist.get(1));
		b = calcLine(worklist.get(1),worklist.get(2));
		c = calcLine(worklist.get(0),worklist.get(2));
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		double angle = Math.acos((b*b + a*a - c*c)/(2.0*b*a));
		System.out.println(angle);
		return Math.toDegrees(angle);
	}
	private String[] shuffleStringArr (String arr[]){
		String secondArr[] = new String[3];
		
		secondArr[0] = arr[2];
		secondArr[1] = arr[0];
		secondArr[2] = arr[1];
		
		return secondArr;
	}
	private String calcAngles(List<DrawingPoint> points, String[] arr) {
		
		double temp1 = calcAngle(points, arr);
		arr = shuffleStringArr (arr);
		double temp2 = calcAngle(points, arr);
		double temp3 = calcAngle(points, shuffleStringArr(arr));
		
		return  Double.toString(temp1) + "," + Double.toString(temp2) + "," + Double.toString(temp3);
	}
	
	private int calcLine(DrawingPoint p1, DrawingPoint p2) {
		int distance = (int) Math.pow(
				(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(
						Math.abs(p1.y - p2.y), 2)), 0.5);
		return distance;
	}
	private void drawLines(final List<DrawingPoint> points, GC gc) {
		if (movePoint != null) {
			movePoint.x = end.x;
			movePoint.y = end.y;
		}
		// if (start != null && end != null) {
		// gc.drawLine(start.x, start.y, end.x, end.y);
		// }
		for (int i = 0; i < points.size(); i++) {
			DrawingPoint p1 = points.get(i);
			gc.drawOval(p1.x - 4, p1.y - 4, 8, 8);
			gc.drawText(p1.title, p1.x + 4, p1.y + 4);
			for (int k = i + 1; k < points.size(); k++) {
				int distance = calcLine(points.get(i), points.get(k));
				DrawingPoint p2 = points.get(k);
				int xnew;
				int ynew;
				if (p1.x > p2.x) {
					xnew = (p1.x - p2.x) / 2 + p2.x;
				} else {
					xnew = (p2.x - p1.x) / 2 + p1.x;
				}
				if (p1.y > p2.y) {
					ynew = (p1.y - p2.y) / 2 + p2.y;
				} else {
					ynew = (p2.y - p1.y) / 2 + p1.y;
				}
				gc.drawText(Integer.toString(distance) + "px", xnew, ynew);
				gc.drawLine(p1.x, p1.y, p2.x, p2.y);
			}
		}
		// for (int i = 0; i < points.size(); i++) {
		// Point start = points.get(i);
		// Point end = points.get(++i);
		// gc.drawLine(start.x, start.y, end.x, end.y);
		// }
	}
}
