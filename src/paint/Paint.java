package paint;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Paint extends javax.swing.JFrame {

    private Color brushColor = Color.BLACK;
    private int old_mouse_x = 0;
    private int old_mouse_y = 0;
    private int start_x = 0;
    private int start_y = 0;
    private BufferedImage canvas;
    private Graphics2D canvasGraphics;
    private boolean isDrawing = false;
    private ArrayList<Point> polygonPoints = new ArrayList<>();
    private BufferedImage tempCanvas;

    public Paint() {
        initComponents();
        initializeCanvas();
        addMouseListeners();
    }

    private void initializeCanvas() {
        canvas = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        canvasGraphics = canvas.createGraphics();
        canvasGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        canvasGraphics.setColor(Color.WHITE);
        canvasGraphics.fillRect(0, 0, 800, 600);
        canvasGraphics.setColor(brushColor);
        
        jPanel1.setBackground(Color.WHITE);
        jPanel1.setPreferredSize(new Dimension(800, 600));
    }

    private void addMouseListeners() {        
        jPanel1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                start_x = old_mouse_x = evt.getX();
                start_y = old_mouse_y = evt.getY();
                isDrawing = true;
                
                if (jComboBox1.getSelectedIndex() == 4) {
                    if (evt.getButton() == MouseEvent.BUTTON1) { 
                        polygonPoints.add(new Point(evt.getX(), evt.getY()));
                        repaintPanel();
                    } else if (evt.getButton() == MouseEvent.BUTTON3) { 
                        if (polygonPoints.size() >= 3) {
                            drawPolygon();
                            polygonPoints.clear();
                            repaintPanel();
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (!isDrawing) return;
                isDrawing = false;
                
                int selectedTool = jComboBox1.getSelectedIndex();
                canvasGraphics.setColor(brushColor);
                canvasGraphics.setStroke(new BasicStroke(jComboBox2.getSelectedIndex() + 1));
                
                switch (selectedTool) {
                    case 2: 
                        drawCircle(start_x, start_y, evt.getX(), evt.getY());
                        break;
                    case 3: 
                        drawRectangle(start_x, start_y, evt.getX(), evt.getY());
                        break;
                    case 5: 
                        drawLine(start_x, start_y, evt.getX(), evt.getY());
                        break;
                }
                repaintPanel();
            }
        });

        jPanel1.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                if (jComboBox1.getSelectedIndex() == 0) {
                    canvasGraphics.setColor(brushColor);
                    canvasGraphics.setStroke(new BasicStroke(jComboBox2.getSelectedIndex() + 1));
                    canvasGraphics.drawLine(old_mouse_x, old_mouse_y, evt.getX(), evt.getY());
                    old_mouse_x = evt.getX();
                    old_mouse_y = evt.getY();
                    repaintPanel();
                } else if (jComboBox1.getSelectedIndex() == 1) { 
                    canvasGraphics.setColor(brushColor);
                    canvasGraphics.setStroke(new BasicStroke(jComboBox2.getSelectedIndex() + 3));
                    canvasGraphics.drawLine(old_mouse_x, old_mouse_y, evt.getX(), evt.getY());
                    old_mouse_x = evt.getX();
                    old_mouse_y = evt.getY();
                    repaintPanel();
                }
            }
        });
    }

    private void drawCircle(int x1, int y1, int x2, int y2) {
        int radius = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        canvasGraphics.drawOval(x1 - radius, y1 - radius, 2 * radius, 2 * radius);
    }

    private void drawCirclePreview(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        int radius = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        g2d.drawOval(x1 - radius, y1 - radius, 2 * radius, 2 * radius);
    }

    private void drawRectangle(int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        canvasGraphics.drawRect(x, y, width, height);
    }

    private void drawRectanglePreview(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        g2d.drawRect(x, y, width, height);
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        canvasGraphics.drawLine(x1, y1, x2, y2);
    }

    private void drawPolygon() {
        if (polygonPoints.size() >= 3) {
            canvasGraphics.setColor(brushColor);
            canvasGraphics.setStroke(new BasicStroke(jComboBox2.getSelectedIndex() + 1));
            
            int[] xPoints = new int[polygonPoints.size()];
            int[] yPoints = new int[polygonPoints.size()];
            
            for (int i = 0; i < polygonPoints.size(); i++) {
                xPoints[i] = polygonPoints.get(i).x;
                yPoints[i] = polygonPoints.get(i).y;
            }
            
            canvasGraphics.drawPolygon(xPoints, yPoints, polygonPoints.size());
        }
    }

    private void repaintPanel() {
        SwingUtilities.invokeLater(() -> {
            Graphics2D g2d = (Graphics2D) jPanel1.getGraphics();
            if (g2d != null) {
                g2d.drawImage(canvas, 0, 0, null);
                
                if (jComboBox1.getSelectedIndex() == 4 && !polygonPoints.isEmpty()) {
                    g2d.setColor(Color.RED);
                    for (Point p : polygonPoints) {
                        g2d.fillOval(p.x - 3, p.y - 3, 6, 6);
                    }
                    
                    g2d.setColor(brushColor);
                    g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
                    for (int i = 0; i < polygonPoints.size() - 1; i++) {
                        Point p1 = polygonPoints.get(i);
                        Point p2 = polygonPoints.get(i + 1);
                        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }
        });
    }

    private void clearCanvas() {
        canvasGraphics.setColor(Color.WHITE);
        canvasGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvasGraphics.setColor(brushColor);
        polygonPoints.clear();
        repaintPanel();
    }

    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz obraz");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Obrazy", "png", "jpg", "jpeg", "gif", "bmp");
        fileChooser.setFileFilter(filter);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String fileName = file.getName();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
                
                if (!extension.matches("png|jpg|jpeg|gif|bmp")) {
                    file = new File(file.getAbsolutePath() + ".png");
                    extension = "png";
                }
                
                ImageIO.write(canvas, extension, file);
                JOptionPane.showMessageDialog(this, "Obraz został zapisany!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Błąd podczas zapisywania: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wczytaj obraz");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Obrazy", "png", "jpg", "jpeg", "gif", "bmp");
        fileChooser.setFileFilter(filter);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage loadedImage = ImageIO.read(fileChooser.getSelectedFile());
                if (loadedImage != null) {
                    canvasGraphics.setColor(Color.WHITE);
                    canvasGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    canvasGraphics.drawImage(loadedImage, 0, 0, null);
                    canvasGraphics.setColor(brushColor);
                    repaintPanel();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Błąd podczas wczytywania: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    @SuppressWarnings("unchecked")
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Paint Design - Enhanced Version");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ołówek", "Pisak", "Koło", "Prostokąt", "Wielokąt", "Linia prosta" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1px", "2px", "3px", "4px", "5px" }));

        jButton1.setText("Kolor");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);
        jTextField1.setBackground(Color.BLACK);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton2.setText("Wyczyść");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Zapisz");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Wczytaj");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 796, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 596, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }                     

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (jComboBox1.getSelectedIndex() != 4) {
            polygonPoints.clear();
            repaintPanel();
        }
        
        if (jComboBox1.getSelectedIndex() == 4) {
            JOptionPane.showMessageDialog(this, 
                "Tryb wielokąta:\n• Lewy klik - dodaj punkt\n• Prawy klik - zakończ wielokąt (min. 3 punkty)", 
                "Instrukcja", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        Color newColor = JColorChooser.showDialog(null, "Wybierz kolor", brushColor);
        if (newColor != null) {
            brushColor = newColor;
            jTextField1.setBackground(brushColor);
            canvasGraphics.setColor(brushColor);
        }
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        int result = JOptionPane.showConfirmDialog(this, 
            "Czy na pewno chcesz wyczyścić płótno?", 
            "Potwierdzenie", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            clearCanvas();
        }
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        saveImage();
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        loadImage();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (canvas != null) {
            Graphics2D g2d = (Graphics2D) jPanel1.getGraphics();
            if (g2d != null) {
                g2d.drawImage(canvas, 0, 0, null);
            }
        }
    }


    public static void main(String args[]) {
      
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Paint().setVisible(true);
            }
        });
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
}