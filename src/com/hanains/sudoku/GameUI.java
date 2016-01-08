package com.hanains.sudoku;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class GameUI extends Frame implements WindowListener {

	// Text Field, Button, CheckBox
	private JPanel[] boxPanel = new JPanel[9];
	private TextField[] numBox = new TextField[81];
	private Button doneButton = new Button("Check");
	private Button startButton = new Button("Start");
	private Button submitButton = new Button("확인");
	private CheckboxGroup levelChG = new CheckboxGroup();
	private Checkbox level1Ch = new Checkbox("LV1", true, levelChG);
	private Checkbox level2Ch = new Checkbox("LV2", false, levelChG);
	private Checkbox level3Ch = new Checkbox("LV3", false, levelChG);
	private Dialog dialog = null;

	// 박스 색 지정
	private static Color spaceBoxCol = (Color.orange);
	private static Color spaceTextCol = (Color.white);
	private static Color staticBoxCol = (Color.cyan);
	private static Color staticTextCol = (Color.darkGray);
	private static Color inputBoxCol = (Color.magenta);
	private static Color inputTextCol = (Color.black);
	private static Color errorBoxCol = (Color.yellow);
	private static Color errorTextCol = (Color.black);
	private static Color correctBoxCol = (Color.cyan);
	private static Color correctTextCol = (Color.red);

	private SudokuLogic logic;

	class AcListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			// 제어 버튼 누를 때 발생하는 이벤트
			if (event.getSource() == doneButton) {
				// 체크 버튼으로 완료시점이나 틀린 것이 없나 검사하는 부분
				System.out.println("CheckButton");

				int leftBox = checkResult();

				if (leftBox > 0)
					confirmDialog(leftBox + "개 남았습니다.");
				else
					confirmDialog("모두 맞추셨습니다.");
			} else if (event.getSource() == submitButton) {
				// Dialog 확인 버튼
				dialog.setVisible(false);
			} else if (event.getSource() == startButton) {
				// 게임을 시작하는 부분
				Checkbox curChBox = levelChG.getSelectedCheckbox();
				int level = 0;

				System.out.println("startButton");

				// 현재 선택되어 있는 레벨에 따라 난이도 결정
				if (curChBox == level1Ch)
					level = logic.level1;
				else if (curChBox == level2Ch)
					level = logic.level2;
				else
					level = logic.level3;

				startGame(level);
			} else // 빈칸에 숫자 넣는 이벤트
			{
				TextField SourceNum = (TextField) event.getSource();
				// System.out.println( SourceNum.getText() );
				SourceNum.selectAll();
			}
		}
	}
	
	public GameUI( SudokuLogic logic )
	{
		int	i = 0;

		this.logic = logic;
		
		
		this.addWindowListener(this);
		this.setTitle("Sudoku Game");

		
		// 9x9 박스 화면 구성
		JPanel numBoxP = new JPanel( new GridLayout(3, 3) );
		
		int gap = 0;
		int pGap = 0;
		int pIndex = 0;
		
		for( i = 0 ; i < boxPanel.length ; i++){
			JPanel panel = new JPanel( new GridLayout(3,3) );
			panel.setBorder(BorderFactory.createLineBorder(Color.black));
			panel.setFont(new Font("SansSerif", Font.BOLD, 26));
			boxPanel[i] = panel;
		}
	
		for( i = 0 ; i < logic.totalCell ; i++ )
		{
			numBox[i] = new TextField( 1 );
			numBox[i].setColumns( 1 );
			numBox[i].setSize( 4, 4 );
			
			numBox[i].setBackground( staticBoxCol  );				
			numBox[i].setForeground( staticTextCol  );
			
			// 이벤트 연결
			numBox[i].addActionListener( new AcListener() );
			
			if( i!=0 && i%9==0 ) gap += 3;
			
			if( i!=0 && i%27==0 ){
				gap=0; 
				pGap += 6;
			}
			
			pIndex = (i/3)-(gap+pGap);
			
			boxPanel[pIndex].add(numBox[i]);
		}
		
		for( i = 0 ; i < boxPanel.length ; i++){
			numBoxP.add("Center", boxPanel[i]);
		}
		
		// Logo
		String	title = "SUDOKU", subTitle1 = "BY", subTitle2 = "ROXY";
		for( i = 0 ; i < title.length() ; i++ )
			numBox[1+i+2*9].setText( title.substring(i, i+1) );
		for( i = 0 ; i < subTitle1.length() ; i++ )
			numBox[3+i+4*9].setText( subTitle1.substring(i, i+1) );
		for( i = 0 ; i < subTitle2.length() ; i++ )
			numBox[5+i+6*9].setText( subTitle2.substring(i, i+1) );
		
		
		
		
		
		// 제어 버튼 구성
		//Panel ctrlP = new Panel( new BorderLayout() );
		Panel ctrlP = new Panel( new GridLayout(1,3) );

		// 레벨 선택 버튼
		ctrlP.add( level1Ch );
		ctrlP.add( level2Ch );
		ctrlP.add( level3Ch );

		// 시작 버튼
		ctrlP.add( startButton );
		startButton.addActionListener( new AcListener() );
		
		// 완료 버튼
		ctrlP.add( doneButton );
		doneButton.addActionListener( new AcListener() );
		
		this.setLayout( new FlowLayout(FlowLayout.CENTER, 100, 10) );
		this.add( numBoxP );
		this.add( ctrlP );
	}
	
	
	// 칸에 숫자를 채움
	public void setNumberField( int index, int number )
	{
		String	strNum = Integer.toString( number );
		
		if( number == 0 || number > logic.blockCell )
			return;
		
		numBox[index].setText( strNum );
		numBox[index].validate();
		
		numBox[index].setEditable( false );
	}

	
	// 정답과 비교해봄
	public int checkResult()
	{
		int	i = 0;
		String	num = null;
		int	leftBox = 0;
		
		for( i = 0 ; i < logic.totalCell ; i++ )
		{
			if( logic.getBlindNumber(i) != 0 )
				continue;
			
			num = Integer.toString( logic.getOriginalNumber(i) );

			System.out.println(numBox[i].getText());
			
			if( num.compareTo(numBox[i].getText()) != 0 )	// error
			{
				// 틀렸을 경우 'E'를 표시한다.
				numBox[i].setText( "E" );
				numBox[i].setBackground( errorBoxCol );
				numBox[i].setForeground( errorTextCol );
				numBox[i].validate();
				leftBox++;
			}
			else	// correct
			{				
				numBox[i].setBackground( correctBoxCol );
				numBox[i].setForeground( correctTextCol );
				numBox[i].setEditable( false );
			}
		}
		
		return	leftBox;
	}
	
	
	// 난이도를 선택 했을 경우 문제 출제
	public	void	startGame( int level )
	{		
		// 맵 생성
		logic.autoMakeMap( level );
			
		// 맵 출력
		logic.printOriginalMap();
		
		
		// 맵 빈칸에 현재 문제를 적용시킴
		int	index = 0, data = 0;
	
		for( index = 0 ; index < logic.totalCell ; index++ )
		{
			data = logic.getBlindNumber( index );

			if( data == 0 )
			{
				numBox[index].setText( "" );
				numBox[index].setBackground( spaceBoxCol );				
				numBox[index].setForeground( spaceTextCol );
				numBox[index].setEditable( true );
				continue;
			}

			numBox[index].setText( Integer.toString(data) );
			numBox[index].setBackground( staticBoxCol );
			numBox[index].setForeground( staticTextCol );
			
			numBox[index].setEditable( false );
		}
	}
	
	// 메시지 박스
	public void confirmDialog( String msg )
	{
        if( "".equals( msg ) == false ) 
        {
        	dialog = new Dialog(new Frame(),"확인");
        	
        	// 중앙에 띄우기
        	dialog.setBounds(this.getX()+(this.getSize().width/2-100),
        				this.getY() + (this.getSize().height/2-50), 200, 100);
        	
            Panel p = new Panel();
            
            p.add( new Label(msg) );
            p.add( submitButton );
            
            submitButton.addActionListener( new AcListener() );
            
            dialog.add( p );
            dialog.pack();
            dialog.setVisible( true );
        }
    }

	
	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {	}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {	}
	
}
