package dao;

import koneksi.Koneksi;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class JasperReportDAO {

    public Object buatLaporan(InputStream reportStream) throws Exception {
        return buatLaporan(reportStream, new HashMap<>());
    }

    public Object buatLaporan(InputStream reportStream, Map<String, Object> parameter) throws Exception {
        if (reportStream == null) {
            throw new IllegalArgumentException("File report tidak ditemukan.");
        }

        Class<?> compileManagerClass = Class.forName("net.sf.jasperreports.engine.JasperCompileManager");
        Class<?> jasperReportClass = Class.forName("net.sf.jasperreports.engine.JasperReport");
        Class<?> fillManagerClass = Class.forName("net.sf.jasperreports.engine.JasperFillManager");
        Method compileReport = compileManagerClass.getMethod("compileReport", InputStream.class);
        Method fillReport = fillManagerClass.getMethod(
                "fillReport",
                jasperReportClass,
                Map.class,
                Connection.class
        );

        try (Connection connection = Koneksi.getConnection()) {
            try {
                Object jasperReport = compileReport.invoke(null, reportStream);
                return fillReport.invoke(null, jasperReport, parameter, connection);
            } catch (InvocationTargetException ex) {
                Throwable cause = ex.getCause();
                throw new Exception(cause == null ? ex.getMessage() : pesanException(cause), cause == null ? ex : cause);
            }
        }
    }

    public Object buatLaporanAnggota(InputStream reportStream) throws Exception {
        return buatLaporan(reportStream);
    }

    public Object buatLaporanSimpanan(InputStream reportStream) throws Exception {
        return buatLaporan(reportStream);
    }

    private String pesanException(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            return throwable.getClass().getName();
        }
        return throwable.getClass().getName() + ": " + message;
    }
}
