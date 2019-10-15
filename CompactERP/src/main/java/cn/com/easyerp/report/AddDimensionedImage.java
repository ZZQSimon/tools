package cn.com.easyerp.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.util.*;

public class AddDimensionedImage {
    public static final int EXPAND_ROW = 1;
    public static final int EXPAND_COLUMN = 2;
    public static final int EXPAND_ROW_AND_COLUMN = 3;
    public static final int OVERLAY_ROW_AND_COLUMN = 7;
    private static final int EMU_PER_MM = 36000;

    public void addImageToSheet(final String cellNumber, final Sheet sheet, final Drawing drawing, final URL imageFile,
            final double reqImageWidthMM, final double reqImageHeightMM, final int resizeBehaviour)
            throws IOException, IllegalArgumentException {
        final CellReference cellRef = new CellReference(cellNumber);
        this.addImageToSheet(cellRef.getCol(), cellRef.getRow(), sheet, drawing, imageFile, reqImageWidthMM,
                reqImageHeightMM, resizeBehaviour);
    }

    public void addImageToSheet(final int colNumber, final int rowNumber, final Sheet sheet, final Drawing drawing,
            final URL imageFile, final double reqImageWidthMM, final double reqImageHeightMM, final int resizeBehaviour)
            throws IOException, IllegalArgumentException {
        int imageType = 0;
        final String sURL = imageFile.toString().toLowerCase(Locale.ROOT);
        if (sURL.endsWith(".png")) {
            imageType = 6;
        } else {
            if (!sURL.endsWith("jpg") && !sURL.endsWith(".jpeg")) {
                throw new IllegalArgumentException("Invalid Image file : " + sURL);
            }
            imageType = 5;
        }
        final byte[] img = IOUtils.toByteArray(imageFile.openStream());
        this.addImageToSheet(colNumber, rowNumber, sheet, drawing, imageType, img, reqImageWidthMM, reqImageHeightMM,
                resizeBehaviour);
    }

    public void addImageToSheet(final int colNumber, final int rowNumber, final Sheet sheet, final Drawing drawing,
            final int imageType, final byte[] img, final double reqImageWidthMM, final double reqImageHeightMM,
            final int resizeBehaviour) throws IOException, IllegalArgumentException {
        ClientAnchor anchor = null;
        ClientAnchorDetail rowClientAnchorDetail = null;
        ClientAnchorDetail colClientAnchorDetail = null;
        if (resizeBehaviour != 2 && resizeBehaviour != 1 && resizeBehaviour != 3 && resizeBehaviour != 7) {
            throw new IllegalArgumentException(
                    "Invalid value passed to the resizeBehaviour parameter of AddDimensionedImage.addImageToSheet()");
        }
        colClientAnchorDetail = this.fitImageToColumns(sheet, colNumber, reqImageWidthMM, resizeBehaviour);
        rowClientAnchorDetail = this.fitImageToRows(sheet, rowNumber, reqImageHeightMM, resizeBehaviour);
        anchor = sheet.getWorkbook().getCreationHelper().createClientAnchor();
        anchor.setDx1(0);
        anchor.setDy1(0);
        anchor.setDx2(colClientAnchorDetail.getInset());
        anchor.setDy2(rowClientAnchorDetail.getInset());
        anchor.setCol1(colClientAnchorDetail.getFromIndex());
        anchor.setRow1(rowClientAnchorDetail.getFromIndex());
        anchor.setCol2(colClientAnchorDetail.getToIndex());
        anchor.setRow2(rowClientAnchorDetail.getToIndex());
        anchor.setAnchorType(ClientAnchor.MOVE_AND_RESIZE);
        final int index = sheet.getWorkbook().addPicture(img, imageType);
        drawing.createPicture(anchor, index);
    }

    private ClientAnchorDetail fitImageToColumns(final Sheet sheet, final int colNumber, final double reqImageWidthMM,
            final int resizeBehaviour) {
        double colWidthMM = 0.0;
        double colCoordinatesPerMM = 0.0;
        int pictureWidthCoordinates = 0;
        ClientAnchorDetail colClientAnchorDetail = null;
        colWidthMM = ConvertImageUnits.widthUnits2Millimetres((short) sheet.getColumnWidth(colNumber));
        if (colWidthMM < reqImageWidthMM) {
            if (resizeBehaviour == 2 || resizeBehaviour == 3) {
                sheet.setColumnWidth(colNumber, ConvertImageUnits.millimetres2WidthUnits(reqImageWidthMM));
                if (sheet instanceof HSSFSheet) {
                    colWidthMM = reqImageWidthMM;
                    colCoordinatesPerMM = 1023.0 / colWidthMM;
                    pictureWidthCoordinates = (int) (reqImageWidthMM * colCoordinatesPerMM);
                } else {
                    pictureWidthCoordinates = (int) reqImageWidthMM * 36000;
                }
                colClientAnchorDetail = new ClientAnchorDetail(colNumber, colNumber, pictureWidthCoordinates);
            } else if (resizeBehaviour == 7 || resizeBehaviour == 1) {
                colClientAnchorDetail = this.calculateColumnLocation(sheet, colNumber, reqImageWidthMM);
            }
        } else {
            if (sheet instanceof HSSFSheet) {
                colCoordinatesPerMM = 1023.0 / colWidthMM;
                pictureWidthCoordinates = (int) (reqImageWidthMM * colCoordinatesPerMM);
            } else {
                pictureWidthCoordinates = (int) reqImageWidthMM * 36000;
            }
            colClientAnchorDetail = new ClientAnchorDetail(colNumber, colNumber, pictureWidthCoordinates);
        }
        return colClientAnchorDetail;
    }

    private ClientAnchorDetail fitImageToRows(final Sheet sheet, final int rowNumber, final double reqImageHeightMM,
            final int resizeBehaviour) {
        Row row = null;
        double rowHeightMM = 0.0;
        double rowCoordinatesPerMM = 0.0;
        int pictureHeightCoordinates = 0;
        ClientAnchorDetail rowClientAnchorDetail = null;
        row = sheet.getRow(rowNumber);
        if (row == null) {
            row = sheet.createRow(rowNumber);
        }
        rowHeightMM = row.getHeightInPoints() / 2.83;
        if (rowHeightMM < reqImageHeightMM) {
            if (resizeBehaviour == 1 || resizeBehaviour == 3) {
                row.setHeightInPoints((float) (reqImageHeightMM * 2.83));
                if (sheet instanceof HSSFSheet) {
                    rowHeightMM = reqImageHeightMM;
                    rowCoordinatesPerMM = 255.0 / rowHeightMM;
                    pictureHeightCoordinates = (int) (reqImageHeightMM * rowCoordinatesPerMM);
                } else {
                    pictureHeightCoordinates = (int) (reqImageHeightMM * 36000.0);
                }
                rowClientAnchorDetail = new ClientAnchorDetail(rowNumber, rowNumber, pictureHeightCoordinates);
            } else if (resizeBehaviour == 7 || resizeBehaviour == 2) {
                rowClientAnchorDetail = this.calculateRowLocation(sheet, rowNumber, reqImageHeightMM);
            }
        } else {
            if (sheet instanceof HSSFSheet) {
                rowCoordinatesPerMM = 255.0 / rowHeightMM;
                pictureHeightCoordinates = (int) (reqImageHeightMM * rowCoordinatesPerMM);
            } else {
                pictureHeightCoordinates = (int) (reqImageHeightMM * 36000.0);
            }
            rowClientAnchorDetail = new ClientAnchorDetail(rowNumber, rowNumber, pictureHeightCoordinates);
        }
        return rowClientAnchorDetail;
    }

    private ClientAnchorDetail calculateColumnLocation(final Sheet sheet, final int startingColumn,
            final double reqImageWidthMM) {
        ClientAnchorDetail anchorDetail = null;
        double totalWidthMM = 0.0;
        double colWidthMM = 0.0;
        double overlapMM = 0.0;
        double coordinatePositionsPerMM = 0.0;
        int toColumn = startingColumn;
        int inset = 0;
        while (totalWidthMM < reqImageWidthMM) {
            colWidthMM = ConvertImageUnits.widthUnits2Millimetres((short) sheet.getColumnWidth(toColumn));
            totalWidthMM += colWidthMM + 2.0;
            ++toColumn;
        }
        --toColumn;
        if ((int) totalWidthMM == (int) reqImageWidthMM) {
            if (sheet instanceof HSSFSheet) {
                anchorDetail = new ClientAnchorDetail(startingColumn, toColumn, 1023);
            } else {
                anchorDetail = new ClientAnchorDetail(startingColumn, toColumn, (int) reqImageWidthMM * 36000);
            }
        } else {
            overlapMM = reqImageWidthMM - (totalWidthMM - colWidthMM);
            if (overlapMM < 0.0) {
                overlapMM = 0.0;
            }
            if (sheet instanceof HSSFSheet) {
                coordinatePositionsPerMM = 1023.0 / colWidthMM;
                inset = (int) (coordinatePositionsPerMM * overlapMM);
            } else {
                inset = (int) overlapMM * 36000;
            }
            anchorDetail = new ClientAnchorDetail(startingColumn, toColumn, inset);
        }
        return anchorDetail;
    }

    private ClientAnchorDetail calculateRowLocation(final Sheet sheet, final int startingRow,
            final double reqImageHeightMM) {
        ClientAnchorDetail clientAnchorDetail = null;
        Row row = null;
        double rowHeightMM = 0.0;
        double totalRowHeightMM = 0.0;
        double overlapMM = 0.0;
        double rowCoordinatesPerMM = 0.0;
        int toRow = startingRow;
        int inset = 0;
        while (totalRowHeightMM < reqImageHeightMM) {
            row = sheet.getRow(toRow);
            if (row == null) {
                row = sheet.createRow(toRow);
            }
            rowHeightMM = row.getHeightInPoints() / 2.83;
            totalRowHeightMM += rowHeightMM;
            ++toRow;
        }
        --toRow;
        if ((int) totalRowHeightMM == (int) reqImageHeightMM) {
            if (sheet instanceof HSSFSheet) {
                clientAnchorDetail = new ClientAnchorDetail(startingRow, toRow, 255);
            } else {
                clientAnchorDetail = new ClientAnchorDetail(startingRow, toRow, (int) reqImageHeightMM * 36000);
            }
        } else {
            overlapMM = reqImageHeightMM - (totalRowHeightMM - rowHeightMM);
            if (overlapMM < 0.0) {
                overlapMM = 0.0;
            }
            if (sheet instanceof HSSFSheet) {
                rowCoordinatesPerMM = 255.0 / rowHeightMM;
                inset = (int) (overlapMM * rowCoordinatesPerMM);
            } else {
                inset = (int) overlapMM * 36000;
            }
            clientAnchorDetail = new ClientAnchorDetail(startingRow, toRow, inset);
        }
        return clientAnchorDetail;
    }

    public static void main(final String[] args) {
        String imageFile = null;
        String outputFile = null;
        FileOutputStream fos = null;
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            if (args.length < 2) {
                System.err.println("Usage: AddDimensionedImage imageFile outputFile");
                return;
            }
            workbook = (Workbook) new HSSFWorkbook();
            sheet = workbook.createSheet("Picture Test");
            imageFile = args[0];
            outputFile = args[1];
            new AddDimensionedImage().addImageToSheet("B5", sheet, sheet.createDrawingPatriarch(),
                    new File(imageFile).toURI().toURL(), 100.0, 40.0, 3);
            fos = new FileOutputStream(outputFile);
            workbook.write((OutputStream) fos);
        } catch (FileNotFoundException fnfEx) {
            System.out.println("Caught an: " + fnfEx.getClass().getName());
            System.out.println("Message: " + fnfEx.getMessage());
            System.out.println("Stacktrace follows...........");
            fnfEx.printStackTrace(System.out);
        } catch (IOException ioEx) {
            System.out.println("Caught an: " + ioEx.getClass().getName());
            System.out.println("Message: " + ioEx.getMessage());
            System.out.println("Stacktrace follows...........");
            ioEx.printStackTrace(System.out);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException ex) {
                }
            }
        }
    }

    public class ClientAnchorDetail {
        public int fromIndex;
        public int toIndex;
        public int inset;

        public ClientAnchorDetail(final int fromIndex, final int toIndex, final int inset) {
            this.fromIndex = 0;
            this.toIndex = 0;
            this.inset = 0;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.inset = inset;
        }

        public int getFromIndex() {
            return this.fromIndex;
        }

        public int getToIndex() {
            return this.toIndex;
        }

        public int getInset() {
            return this.inset;
        }
    }

    public static class ConvertImageUnits {
        public static final int TOTAL_COLUMN_COORDINATE_POSITIONS = 1023;
        public static final int TOTAL_ROW_COORDINATE_POSITIONS = 255;
        public static final int PIXELS_PER_INCH = 96;
        public static final double PIXELS_PER_MILLIMETRES = 3.78;
        public static final double POINTS_PER_MILLIMETRE = 2.83;
        public static final double CELL_BORDER_WIDTH_MILLIMETRES = 2.0;
        public static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
        public static final int UNIT_OFFSET_LENGTH = 7;
        public static final int[] UNIT_OFFSET_MAP;

        public static short pixel2WidthUnits(final int pxs) {
            short widthUnits = (short) (256 * (pxs / 7));
            widthUnits += (short) ConvertImageUnits.UNIT_OFFSET_MAP[pxs % 7];
            return widthUnits;
        }

        public static int widthUnits2Pixel(final short widthUnits) {
            int pixels = widthUnits / 256 * 7;
            final int offsetWidthUnits = widthUnits % 256;
            pixels += Math.round(offsetWidthUnits / 36.57143f);
            return pixels;
        }

        public static double widthUnits2Millimetres(final short widthUnits) {
            return widthUnits2Pixel(widthUnits) / 3.78;
        }

        public static int millimetres2WidthUnits(final double millimetres) {
            return pixel2WidthUnits((int) (millimetres * 3.78));
        }

        public static int pointsToPixels(final double points) {
            return (int) Math.round(points / 72.0 * 96.0);
        }

        public static double pointsToMillimeters(final double points) {
            return points / 72.0 * 25.4;
        }

        static {
            UNIT_OFFSET_MAP = new int[] { 0, 36, 73, 109, 146, 182, 219 };
        }
    }
}
