package com.pearson.lagp.v3;

import java.util.ArrayList;

import org.anddev.andengine.entity.modifier.PathModifier.Path;

import android.util.Log;

public class AStar {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private GridLoc[][] grid;
	private int rowMax, colMax;
	private int cellWidth, cellHeight;
	private String tag = "AStar:";
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public AStar(int pRows, int pCols, int pWidth, int pHeight) {
		// pWidth = total width in pixels
		// pHeight = total height in pixels
		grid = new GridLoc[pRows][pCols];
		rowMax = pRows-1;
		colMax = pCols-1;
		cellWidth = pWidth/pCols;
		cellHeight = pHeight/pRows;
	    for (int i=0; i<pRows; i++) {
	    	for (int j=0; j<pCols; j++) {
	    		grid[i][j] = new GridLoc();
	    	}
	   	}
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public Path getPath(float pStartX, int pTargetCol, float pStartY, int pTargetRow, float pSpriteWidth, float pSpriteHeight){
		// Use A* pathfinding to find the near optimal path
		int nextCol, nextRow;
		int startCol, startRow;
		ArrayList<Integer> pathCols = new ArrayList<Integer>();
		ArrayList<Integer> pathRows = new ArrayList<Integer>();
		startCol = (int)pStartX/cellWidth;
		startRow = (int)pStartY/cellHeight;
		int currCol = startCol;
		int currRow = startRow;
		float[] f = new float[8];
		grid[currRow][currCol].g = 0.0f;
		grid[currRow][currCol].h = pTargetCol - currCol + pTargetRow - currRow;
		grid[currRow][currCol].footprint = true;
		
		while ((currCol != pTargetCol) || (currRow != pTargetRow)){
			//Consider the eight surrounding locations
			for (int i=0; i<8; i++) f[i] = 0;
			
			f[0] = fComp(currRow, currCol, -1, -1, 1.4f, pTargetRow, pTargetCol);
			f[1] = fComp(currRow, currCol, 0, -1, 1.0f, pTargetRow, pTargetCol);
			f[2] = fComp(currRow, currCol, +1, -1, 1.4f, pTargetRow, pTargetCol);
			f[3] = fComp(currRow, currCol, -1, 0, 1.0f, pTargetRow, pTargetCol);
			f[4] = fComp(currRow, currCol, +1, 0, 1.0f, pTargetRow, pTargetCol);
			f[5] = fComp(currRow, currCol, -1, +1, 1.4f, pTargetRow, pTargetCol);
			f[6] = fComp(currRow, currCol, 0, +1, 1.0f, pTargetRow, pTargetCol);
			f[7] = fComp(currRow, currCol, +1, +1, 1.4f, pTargetRow, pTargetCol);
			
			int lowidx = 0;
			float pos = 10000.0f;
			for (int j=0; j<8; j++){
				if (f[j]<pos){
					pos = f[j];
					lowidx = j;
				}
			}
			nextCol = currCol;
			nextRow = currRow;
			switch (lowidx){
				case (0):
					nextRow = currRow - 1;
					nextCol = currCol - 1;
					break;
				case (1):
					nextRow = currRow;
					nextCol = currCol - 1;
					break;
				case (2):
					nextRow = currRow + 1;
					nextCol = currCol - 1;
					break;
				case (3):
					nextRow = currRow - 1;
					nextCol = currCol;
					break;
				case (4):
					nextRow = currRow + 1;
					nextCol = currCol;
					break;
				case (5):
					nextRow = currRow - 1;
					nextCol = currCol + 1;
					break;
				case (6):
					nextRow = currRow;
					nextCol = currCol + 1;
					break;
				case (7):
					nextRow = currRow + 1;
					nextCol = currCol + 1;
					break;
			}
			//Add next location to Path, set footprint and update currCol, currRow
			pathCols.add(nextCol);
			pathRows.add(nextRow);
			if ((currRow > 0) && (currRow < rowMax) && (currCol > 0) && (currCol < colMax)) {
				grid[currRow][currCol].footprint = true;
			}
			currCol = nextCol;
			currRow = nextRow;
			Log.d(tag, "currCol: "+currCol+" currRow: "+currRow);
		}
		float[] xArray = new float[pathCols.size()+1];
		float[] yArray = new float[pathRows.size()+1];
		xArray[0] = pStartX;
		yArray[0] = pStartY;


		for (int i = 1; i < xArray.length; i++) {
		    Float tmpX = (float)pathCols.get(i-1) * cellWidth - pSpriteWidth/2;
		    xArray[i] = (tmpX != null ? tmpX : 0.0f); 
		    Float tmpY = (float)pathRows.get(i-1) * cellHeight - pSpriteHeight/2;
		    yArray[i] = (tmpY != null ? tmpY : 0.0f); 
		}
		return (new Path(xArray, yArray));
	}
	
	public void setObstacle(int pObstacleRow, int pObstacleCol){
		if (grid != null){
			grid[pObstacleRow][pObstacleCol].obstacle = true;
		}
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private float fComp(int pCurrRow, int pCurrCol, int pRowDiff, int pColDiff, float pDx, int pTargetRow, int pTargetCol){
		// Computes the A* values for a grid location:
		// 	g: distance from start
		//  h: distance to target
		// returns f = g + h
		// If the grid location is marked as an obstacle, footprint is true, or it is outside the grid, returns a high number (5,000)
		if (((pCurrRow + pRowDiff) > rowMax) || 
				((pCurrCol + pColDiff) > colMax) ||
				((pCurrRow + pRowDiff) < 0) ||
				((pCurrCol + pColDiff) < 0)) {
			return 5000.0f;
		}
		if((grid[pCurrRow + pRowDiff][pCurrCol + pColDiff].obstacle) ||
			(grid[pCurrRow + pRowDiff][pCurrCol + pColDiff].footprint)){
			return 5000.0f;
		}
		
		grid[pCurrRow + pRowDiff][pCurrCol + pColDiff].g = grid[pCurrRow][pCurrCol].g + pDx;
		grid[pCurrRow + pRowDiff][pCurrCol + pColDiff].h = Math.abs(pTargetRow - (pCurrRow + pRowDiff)) + Math.abs(pTargetCol - (pCurrCol + pColDiff));
		return (grid[pCurrRow + pRowDiff][pCurrCol + pColDiff].g + grid[pCurrRow + pRowDiff][pCurrCol + pColDiff].h);
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
