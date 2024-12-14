package bsu.rfe.java.group6.lab4.tuz.var19;

import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;
    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis = true;
    private boolean showMarkers = true;
    // Границы диапазона пространства, подлежащего отображению
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    // Используемый масштаб отображения
    private double scale;
    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    // Различные шрифты отображения надписей
    private Font axisFont;

    public GraphicsDisplay() {
        // Цвет заднего фона области отображения - белый
        setBackground(Color.WHITE);
// Сконструировать необходимые объекты, используемые в рисовании
// Перо для рисования графика
        graphicsStroke = new BasicStroke(3f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f,
                new float[] {17,6,9,6,9,6,4,6 }, 0.0f);

// Перо для рисования осей координат
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Перо для рисования контуров маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Шрифт для подписей осей координат
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    // Данный метод вызывается из обработчика элемента меню "Открыть файл с графиком"
    // главного окна приложения в случае успешной загрузки данных
    public void showGraphics(Double[][] graphicsData) {
// Сохранить массив точек во внутреннем поле класса
        this.graphicsData = graphicsData;
// Запросить перерисовку компонента, т.е. неявно вызвать paintComponent()
        repaint();
    }

    // Методы-модификаторы для изменения параметров отображения графика
// Изменение любого параметра приводит к перерисовке области
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    // Метод отображения всего компонента, содержащего график
    public void paintComponent(Graphics g) {

        /* Шаг 1 - Вызвать метод предка для заливки области цветом заднего фона
         * Эта функциональность - единственное, что осталось в наследство от
         * paintComponent класса JPanel
         */
        super.paintComponent(g);
// Шаг 2 - Если данные графика не загружены (при показе компонента при запуске программы) - ничего не делать
        if (graphicsData == null || graphicsData.length == 0) return;
// Шаг 3 - Определить минимальное и максимальное значения для координат X и Y
// Это необходимо для определения области пространства, подлежащей отображению
// Еѐ верхний левый угол это (minX, maxY) - правый нижний это (maxX, minY)
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;
// Найти минимальное и максимальное значение функции
        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }

        }
        if (minY > 0) {
            minY = -1;  // Убедитесь, что ось X будет видна
        }
        if (maxY < 0) {
            maxY = 1;   // Убедитесь, что ось X будет видна
        }
        int panelWidth = getSize().width;
        int panelHeight = getSize().height;
/* Шаг 4 - Определить (исходя из размеров окна) масштабы по осям X
и Y - сколько пикселов
* приходится на единицу длины по X и по Y
*/
        double centerX= panelWidth /2.0;
        double centerY= panelHeight /2.0;
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
// Шаг 5 - Чтобы изображение было неискажѐнным - масштаб должен быть одинаков
// Выбираем за основу минимальный
        scale = Math.min(scaleX, scaleY);
// Шаг 6 - корректировка границ отображаемой области согласно выбранному масштабу
        if (scale == scaleX) {
/* Если за основу был взят масштаб по оси X, значит по оси Y
делений меньше,
* т.е. подлежащий визуализации диапазон по Y будет меньше
высоты окна.
* Значит необходимо добавить делений, сделаем это так:
* 1) Вычислим, сколько делений влезет по Y при выбранном
масштабе - getSize().getHeight()/scale
* 2) Вычтем из этого сколько делений требовалось изначально
* 3) Набросим по половине недостающего расстояния на maxY и
minY
*/
            double yIncrement = (getSize().getHeight() / scale - (maxY -
                    minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {
// Если за основу был взят масштаб по оси Y, действовать по аналогии
            double xIncrement = (getSize().getWidth() / scale - (maxX -
                    minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }
// Шаг 7 - Сохранить текущие настройки холста
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
// Шаг 8 - В нужном порядке вызвать методы отображения элементом графика
// Порядок вызова методов имеет значение, т.к. предыдущий рисунок будет затираться последующим
// Первыми (если нужно) отрисовываются оси координат.
        if (showAxis) paintAxis(canvas);
// Затем отображается сам график
        paintGraphics(canvas);
// Затем (если нужно) отображаются маркеры точек, по который строился график.
        if (showMarkers) paintMarkers(canvas);
// Шаг 9 - Восстановить старые настройки холста
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintGraphics(Graphics2D canvas) {
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
// Выбрать линию для рисования графика
        canvas.setStroke(graphicsStroke);
// Выбрать цвет линии

        canvas.setColor(Color.BLACK);
/* Будем рисовать линию графика как путь, состоящий из множества
сегментов (GeneralPath)
* Начало пути устанавливается в первую точку графика, после чего
прямой соединяется со
* следующими точками
*/
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
// Преобразовать значения (x,y) в точку на экране point
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    graphicsData[i][1]);
            if (i > 0) {
// Не первая итерация цикла - вести линию в точку point
                graphics.lineTo(point.getX(), point.getY());
            } else {
// Первая итерация цикла - установить начало пути в точку point
                graphics.moveTo(point.getX(), point.getY());
            }
        }
// Отобразить график
        canvas.draw(graphics);
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);

        for (Double[] point : graphicsData) {
            boolean num = isEven(point[1].intValue());
            Color markerColor = (num) ? Color.BLUE : Color.BLACK;
            canvas.setColor(markerColor);

            Point2D.Double center = xyToPoint(point[0], point[1]);

            // Установим фиксированные размеры маркера в пикселях
            int x = (int) center.getX(); // Центр маркера по X
            int y = (int) center.getY(); // Центр маркера по Y

            // Рисуем пиксели, соответствующие фигуре маркера

            // 6-я строка, 1 и 11 столбцы
            canvas.fillRect(x - 5, y, 3, 3);
            canvas.fillRect(x + 5, y, 3, 3);

            // 5-я и 7-я строки, 2-3 и 9-10 столбцы
            canvas.fillRect(x - 4, y - 1, 3, 3);
            canvas.fillRect(x - 3, y - 1, 3, 3);
            canvas.fillRect(x + 3, y - 1, 3, 3);
            canvas.fillRect(x + 4, y - 1, 3, 3);

            canvas.fillRect(x - 4, y + 1, 3, 3);
            canvas.fillRect(x - 3, y + 1, 3, 3);
            canvas.fillRect(x + 3, y + 1, 3, 3);
            canvas.fillRect(x + 4, y + 1, 3, 3);

            // 4-я и 8-я строки, 4-5 и 7-8 столбцы
            canvas.fillRect(x - 2, y - 2, 3, 3);
            canvas.fillRect(x - 1, y - 2, 3, 3);
            canvas.fillRect(x + 1, y - 2, 3, 3);
            canvas.fillRect(x + 2, y - 2, 3, 3);

            canvas.fillRect(x - 2, y + 2, 3, 3);
            canvas.fillRect(x - 1, y + 2, 3, 3);
            canvas.fillRect(x + 1, y + 2, 3, 3);
            canvas.fillRect(x + 2, y + 2, 3, 3);

            // 2-я и 10-я строки, 5 и 7 столбцы
            canvas.fillRect(x - 1, y - 4, 3, 3);
            canvas.fillRect(x + 1, y - 4, 3, 3);

            canvas.fillRect(x - 1, y + 4, 3, 3);
            canvas.fillRect(x + 1, y + 4, 3, 3);

            // 1-я и 11-я строки, 6-й столбец
            canvas.fillRect(x, y - 5, 3, 3);
            canvas.fillRect(x, y + 5, 3, 3);

            // 3-я и 11-я строки, 5 и 7 столбцы
            canvas.fillRect(x - 1, y - 3, 3, 3);
            canvas.fillRect(x + 1, y - 3, 3, 3);

            canvas.fillRect(x - 1, y + 3, 3, 3);
            canvas.fillRect(x + 1, y + 3, 3, 3);
        }
    }


    protected void paintAxis(Graphics2D canvas) {
        // Установить особое начертание для осей
        canvas.setStroke(axisStroke);
        // Оси рисуются черным цветом
        canvas.setColor(Color.BLACK);
        // Стрелки заливаются черным цветом
        canvas.setPaint(Color.BLACK);
        // Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
        // Создать объект контекста отображения текста - для получения характеристик устройства (экрана)
        FontRenderContext context = canvas.getFontRenderContext();

        // Определить, должна ли быть видна ось Y на графике
        if (minX <= 0.0 && maxX >= 0.0) {
            // Рисуем ось Y
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));

            // Рисуем черточку для оси Y на уровне 1
            Point2D.Double pointY = xyToPoint(0, 1);
            canvas.draw(new Line2D.Double(pointY.getX() - 5, pointY.getY(), pointY.getX() + 5, pointY.getY()));  // Черточка
            canvas.drawString("1", (float) pointY.getX() + 5, (float) pointY.getY() + 4);  // Подпись

            // Рисуем стрелку оси Y
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            // Подпись к оси Y
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));
        }

        // Определить, должна ли быть видна ось X на графике
        if (minY <= 0.0 && maxY >= 0.0) {
            // Рисуем ось X
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));

            // Рисуем черточку для оси X на уровне 1
            Point2D.Double pointX = xyToPoint(1, 0); // точка для метки 1 на оси X
            canvas.draw(new Line2D.Double(pointX.getX(), pointX.getY() - 5, pointX.getX(), pointX.getY() + 5));  // Черточка
            canvas.drawString("1", (float) pointX.getX() - 20, (float) pointX.getY() + 15);  // Подпись

            // Рисуем стрелку оси X
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            // Подпись к оси X
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));
        }

        // Рисуем метку 0 на пересечении осей
        Point2D.Double origin = xyToPoint(0, 0);
        canvas.drawString("0", (float) origin.getX() + 2, (float) origin.getY() + 2);  // Сдвиг подписи 0
    }


    /* Метод-помощник, осуществляющий преобразование координат.
    * Оно необходимо, т.к. верхнему левому углу холста с координатами
    * (0.0, 0.0) соответствует точка графика с координатами (minX, maxY),
    где
    * minX - это самое "левое" значение X, а
    * maxY - самое "верхнее" значение Y.
    */
    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale *1, deltaY * scale* 1 ); // Уменьшение размера
    }
    /* Метод-помощник, возвращающий экземпляр класса Point2D.Double
     * смещѐнный по отношению к исходному на deltaX, deltaY
     * К сожалению, стандартного метода, выполняющего такую задачу, нет.
     */
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
                                        double deltaY) {
// Инициализировать новый экземпляр точки
        Point2D.Double dest = new Point2D.Double();
// Задать еѐ координаты как координаты существующей точки + заданные смещения
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
    private Boolean isEven(int n) {
        n = Math.abs(n); // Берем модуль числа, чтобы работать с положительными значениями
        if(n%2 == 0) return Boolean.TRUE;
        else return Boolean.FALSE;
    }
}

