//package view;
//
//import model.text.ITextCipher;
//import javax.swing.*;
//import javax.swing.border.*;
//import java.awt.*;
//
//public class SelectorPanel extends JPanel {
//
//	public final JComboBox<String> typeCombo;
//	public final JComboBox<String> algoCombo;
//
//	// Params panel (thay đổi theo thuật toán)
//	public final JPanel paramsPanel;
//
//	// Affine params
//	public final JSpinner spinnerA;
//	public final JSpinner spinnerB;
//	// Shift params
//	public final JSpinner spinnerShift;
//	// Vigenere key
//	public final JTextField keyField;
//	// Rail Fence rails
//	public final JSpinner spinnerRails;
//
//	public SelectorPanel() {
//		setLayout(new BorderLayout(0, 10));
//		setOpaque(false);
//
//		// ── Top row: Type + Algo ─────────────────
//		JPanel topRow = new JPanel(new GridLayout(1, 2, 14, 0));
//		topRow.setOpaque(false);
//
//		typeCombo = makeCombo(ITextCipher.TYPE_TO_ALGOS.keySet().toArray(new String[0]));
//		algoCombo = makeCombo(new String[] { "Affine", "Shift", "Vigenère", "Rail Fence" });
//
//		topRow.add(wrapLabeled("LOẠI MÃ HÓA", typeCombo));
//		topRow.add(wrapLabeled("THUẬT TOÁN", algoCombo));
//
//		add(topRow, BorderLayout.NORTH);
//
//		// --- Params panel ---
//		paramsPanel = new JPanel(new CardLayout());
//		paramsPanel.setOpaque(false);
//
//		// -- Affine card ---
//		spinnerA = makeSpinner(5, 1, 25, 2);
//		spinnerB = makeSpinner(8, 0, 25, 1);
//		JPanel affineCard = new JPanel(new GridLayout(1, 2, 14, 0));
//		affineCard.setOpaque(false);
//		affineCard.add(wrapLabeled("HỆ SỐ  a  (gcd(a,26)=1)", spinnerA));
//		affineCard.add(wrapLabeled("HỆ SỐ  b  (0 – 25)", spinnerB));
//		paramsPanel.add(affineCard, "Affine");
//
//		// -- Shift card ---
//		spinnerShift = makeSpinner(3, 0, 25, 1);
//		JPanel caesarCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
//		caesarCard.setOpaque(false);
//		JPanel shiftWrap = wrapLabeled("ĐỘ DỊCH  (0 – 25)", spinnerShift);
//		shiftWrap.setPreferredSize(new Dimension(300, 60));
//		caesarCard.add(shiftWrap);
//		paramsPanel.add(caesarCard, "Caesar");
//
//		// -- Vigenere card
//		keyField = new JTextField("SECRET");
//		styleTextField(keyField);
//		JPanel vigCard = new JPanel(new BorderLayout());
//		vigCard.setOpaque(false);
//		vigCard.add(wrapLabeled("KHÓA  (chỉ chữ cái a-z)", keyField), BorderLayout.WEST);
//		paramsPanel.add(vigCard, "Vigenère");
//
//		// -- Rail Fence card
//		spinnerRails = makeSpinner(3, 2, 20, 1);
//		JPanel railCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
//		railCard.setOpaque(false);
//		JPanel railWrap = wrapLabeled("SỐ RAILS  (≥ 2)", spinnerRails);
//		railWrap.setPreferredSize(new Dimension(300, 60));
//		railCard.add(railWrap);
//		paramsPanel.add(railCard, "Rail Fence");
//
//		// -- Empty card for "coming soon" algos
//		paramsPanel.add(new JPanel() {
//			{
//				setOpaque(false);
//			}
//		}, "EMPTY");
//
//		add(paramsPanel, BorderLayout.CENTER);
//
//		// ── Listeners ───────────────────────────
//		typeCombo.addActionListener(e -> onTypeChanged());
//		algoCombo.addActionListener(e -> onAlgoChanged());
//	}
//
//	private void onTypeChanged() {
//		String type = (String) typeCombo.getSelectedItem();
//		String[] algos = ITextCipher.TYPE_TO_ALGOS.getOrDefault(type, new String[] { "N/A" });
//		algoCombo.setModel(new DefaultComboBoxModel<>(algos));
//		onAlgoChanged();
//	}
//
//	private void onAlgoChanged() {
//		String algo = (String) algoCombo.getSelectedItem();
//		CardLayout cl = (CardLayout) paramsPanel.getLayout();
//		if (algo == null)
//			return;
//		switch (algo) {
//		case "Affine":
//			cl.show(paramsPanel, "Affine");
//			break;
//		case "Caesar":
//			cl.show(paramsPanel, "Caesar");
//			break;
//		case "Vigenère":
//			cl.show(paramsPanel, "Vigenère");
//			break;
//		case "Rail Fence":
//			cl.show(paramsPanel, "Rail Fence");
//			break;
//		default:
//			cl.show(paramsPanel, "EMPTY");
//			break;
//		}
//	}
//
//	public String getSelectedType() {
//		return (String) typeCombo.getSelectedItem();
//	}
//
//	public String getSelectedAlgo() {
//		return (String) algoCombo.getSelectedItem();
//	}
//
//	public int getAffineA() {
//		return (int) spinnerA.getValue();
//	}
//
//	public int getAffineB() {
//		return (int) spinnerB.getValue();
//	}
//
//	public int getCaesarShift() {
//		return (int) spinnerShift.getValue();
//	}
//
//	public String getVigenereKey() {
//		return keyField.getText().trim();
//	}
//
//	public int getRailCount() {
//		return (int) spinnerRails.getValue();
//	}
//
//	// ── Helpers ─────────────────────────────────
//	private JComboBox<String> makeCombo(String[] items) {
//		JComboBox<String> cb = new JComboBox<>(items);
//		cb.setBackground(MainFrame.BG_INPUT);
//		cb.setForeground(MainFrame.TXT_MAIN);
//		cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
//		cb.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
//		cb.setFocusable(false);
//		cb.setRenderer(new DefaultListCellRenderer() {
//			public Component getListCellRendererComponent(JList<?> list, Object val, int idx, boolean sel,
//					boolean focus) {
//				super.getListCellRendererComponent(list, val, idx, sel, focus);
//				setBackground(sel ? MainFrame.ACCENT.darker() : MainFrame.BG_INPUT);
//				setForeground(MainFrame.TXT_MAIN);
//				setBorder(new EmptyBorder(4, 10, 4, 10));
//				return this;
//			}
//		});
//		return cb;
//	}
//
//	private JSpinner makeSpinner(int val, int min, int max, int step) {
//		JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, step));
//		sp.setBackground(MainFrame.BG_INPUT);
//		sp.setForeground(MainFrame.TXT_MAIN);
//		sp.setFont(new Font("Monospaced", Font.BOLD, 13));
//		((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setBackground(MainFrame.BG_INPUT);
//		((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setForeground(MainFrame.TXT_MAIN);
//		((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setBorder(new EmptyBorder(4, 8, 4, 8));
//		sp.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
//		sp.setPreferredSize(new Dimension(120, 34));
//		return sp;
//	}
//
//	private void styleTextField(JTextField tf) {
//		tf.setBackground(MainFrame.BG_INPUT);
//		tf.setForeground(MainFrame.TXT_MAIN);
//		tf.setCaretColor(MainFrame.ACCENT);
//		tf.setFont(new Font("Monospaced", Font.BOLD, 13));
//		tf.setBorder(new CompoundBorder(new LineBorder(MainFrame.BORDER_CLR), new EmptyBorder(4, 10, 4, 10)));
//		tf.setPreferredSize(new Dimension(260, 34));
//	}
//
//	private JPanel wrapLabeled(String labelText, JComponent comp) {
//		JPanel p = new JPanel(new BorderLayout(0, 5));
//		p.setOpaque(false);
//		JLabel lbl = new JLabel(labelText);
//		lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
//		lbl.setForeground(MainFrame.TXT_MUTED);
//		lbl.setBorder(new EmptyBorder(0, 2, 0, 0));
//		p.add(lbl, BorderLayout.NORTH);
//		p.add(comp, BorderLayout.CENTER);
//		return p;
//	}
//}
