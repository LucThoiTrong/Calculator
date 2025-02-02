package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btnAC, btnC, btnDelete, btnChia,
            btn7, btn8, btn9, btnNhan,
            btn4, btn5, btn6, btnTru,
            btn1, btn2, btn3, btnCong,
            btn0, btnCongTru, btnBang, btnCham;
    // Khai báo input và result phép tính
    private TextView txtInput;
    private TextView txtResult;
    // Khai báo lịch sử phép tính
    private ListView lsvHistory;
    private HistoryAdapter adapter;
    private ArrayList<History> historyList;
    // Khai báo các cờ phép tính (+ - x /)
    private boolean coCong, coTru, coNhan, coChia;
    // Khai báo các cờ liên quan (=, +/-, !, .)
    private boolean coBang;
    private boolean coCongTru;
    private boolean coLoi;
    private boolean coCham;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Ánh xạ
        AnhXa();

        // Khởi tạo lịch sử tính toán
        KhoiTaoLichSu();

        // Khởi tạo các cờ tính toán
        KhoiTaoCacCo();

        // Khởi tạo sự kiện
        KhoiTaoSuKien();
    }

    private void KhoiTaoSuKien() {
        SuKienClickSo();
        SuKienToanTu();
        SuKienAC();
        SuKienC();
        SuKienDEL();
        SuKienBang();
        SuKienCongTru();
        SuKienCham();
    }

    // Ý tưởng:
    // Trước hết để sự kiện này xảy ra => Sự kiện bằng là false
    // Tìm vị trí của toán tự
    // Nếu vt != -1 => đang ở input2
    // Ngược lại => đang ở input1
    // Trường hợp đang ở input2 => kiểm tra xem sự kiện +/- đang true hay false
    // Nếu true => ta sẽ thực hiện việc thêm value và . bên trong (-...)
    private void SuKienCham() {
        btnCham.setOnClickListener(v -> {
            String input = txtInput.getText().toString();
            if(!coBang) {
                int vt;
                if(coCong) {
                    vt = TimViTriKiTuToanTu('+', input);
                } else if(coTru) {
                    vt = TimViTriKiTuToanTu('-', input);
                } else if(coNhan) {
                    vt = TimViTriKiTuToanTu('x', input);
                } else {
                    vt = TimViTriKiTuToanTu('/', input);
                }
                // Ở vị trí thứ 2
                if(vt != -1) {
                    if(coCongTru) {
                        if(!coCham) {
                            String input2 = input.substring(0, input.length()-1);
                            input2 = input2 + "." + input.substring(input.length()-1);
                            txtInput.setText(input2);
                            coCham = true;
                        }  else {
                            int vtCham = TimViTriKiTuToanTu('.', input);
                            String inputView = input.substring(0, vtCham);
                            inputView += ")";
                            txtInput.setText(inputView);
                            coCham = false;
                        }
                    } else {
                        if(!coCham) {
                            txtInput.append(".");
                            coCham = true;
                        } else {
                            int vtCham = TimViTriKiTuToanTu('.', input);
                            String inputView = input.substring(0, vtCham);
                            txtInput.setText(inputView);
                            coCham = false;
                        }
                    }
                }
                // Ở vị trí thứ 1
                else {
                    if(!coCham) {
                        txtInput.append(".");
                        coCham = true;
                    } else {
                        int vtCham = TimViTriKiTuToanTu('.', input);
                        String inputView = input.substring(0, vtCham);
                        txtInput.setText(inputView);
                        coCham = false;
                    }
                }
            }
        });
    }

    // Ý tưởng:
    // TH1: Nếu chưa ấn '='
    // Tìm vị trí xuất hiên của toán tử trong chuỗi
    // Nếu vt != -1 => nghĩa là đang ở input thứ 2
    // Ngược lại => nghĩa là đang ở input thứ 1
    // TH2: Nếu ấn '='
    // txtInput thêm trừ bình thường
    // txtResult:
    // Xét kí tự đầu tiên có trừ hay không
    // Nếu có => loại bỏ
    // Nếu không có => thêm vào
    // Thực hiện lưu lại lịch sử đối với TH2
    private void SuKienCongTru() {
        btnCongTru.setOnClickListener(v -> {
            String input = txtInput.getText().toString();
            if(!coBang) {
                int vt;
                if(coCong) {
                    vt = TimViTriKiTuToanTu('+', input);
                } else if(coTru) {
                    vt = TimViTriKiTuToanTu('-', input);
                } else if(coNhan) {
                    vt = TimViTriKiTuToanTu('x', input);
                } else {
                    vt = TimViTriKiTuToanTu('/', input);
                }
                // Ở giá trị thứ 2
                if(vt!=-1) {
                    if(!coCongTru) {
                        String input2 = input.substring(vt + 1);
                        input2 = "(-" + input2 + ")";
                        input = input.substring(0, vt + 1) + input2;
                        coCongTru = true;
                    } else {
                        input = new StringBuilder(input).reverse().toString().replaceFirst("-","");
                        input = input.replace(")","");
                        input = input.replace("(","");
                        input = new StringBuilder(input).reverse().toString();
                        coCongTru = false;
                    }
                    txtInput.setText(input);
                }
                // Ở giá trị thứ 1
                else {
                    if(!coCongTru) {
                        input = "-" + input;
                        coCongTru = true;
                    } else {
                        input = input.replaceFirst("-","");
                        coCongTru = false;
                    }
                    txtInput.setText(input);
                }
            } else {
                if(!coCongTru) {
                    input = "-(" + input + ")";
                    coCongTru = true;
                } else {
                    input = input.replace("-(", "");
                    input = input.replace(")", "");
                    coCongTru = false;
                }
                String result = txtResult.getText().toString().replace("=","").trim();
                String firstCharacter = result.substring(0,1);
                if(firstCharacter.equals("-")) {
                    result = result.replaceFirst("-","");
                } else {
                    result = "-" + result;
                }
                String viewResult = String.format("= %s", result);
                txtResult.setText(viewResult);
                txtInput.setText(input);
                SetHistory(input, viewResult);
            }
        });
    }

    // Ý tưởng:
    // Thực hiện phép tính nếu người dùng nhập 1 phép tính rồi ấn dấu '='
    // Ngược lại: ví dụ người dùng nhập "25" rồi ấn '='
    private void SuKienBang() {
        btnBang.setOnClickListener(v -> {
            coBang = true;
            if(coCong || coTru || coNhan || coChia) {
                ThucHienPhepTinh();
            }
            else {
                String input = txtInput.getText().toString();
                String inputHienThi = "= " + input;
                txtResult.setText(inputHienThi);
                txtResult.setVisibility(TextView.VISIBLE);
                SetHistory(input, inputHienThi);
            }
        });
    }

    // Ý tưởng:
    // Nếu còn đúng 1 giá trị hoặc coBang là true => đặt lại trạng thái
    // Ngược lại ta lấy kí tự cuối cùng
    // Nếu là operator hoặc chấm ta set trạng thái các operator tương ứng và chấm về false
    // Nếu là sự kiện +/- => ta sẽ xoá (-
    // Cuối cùng ta sẽ lấy giá trị input mới bằng cách subString từ vị trí 0 => vị trí < n-1
    private void SuKienDEL() {
        btnDelete.setOnClickListener(v -> {
            String input = txtInput.getText().toString();
           if(input.length() == 1 || coBang) {
               DatLaiTrangThai();
           } else {
               // Lấy kí tự cuối cùng
               String lastCharacter = input.substring(input.length()-1);
               switch (lastCharacter) {
                   case "+":
                       coCong = false;
                       break;
                   case "-":
                       coTru = false;
                       coCongTru = false;
                       break;
                   case "x":
                       coNhan = false;
                       break;
                   case "/":
                       coChia = false;
                       break;
                   case ")":
                       input = input.replace("(", "");
                       input = new StringBuilder(input).reverse().toString().replaceFirst("-","");
                       input = new StringBuilder(input).reverse().toString();
                       coCongTru = false;
                       break;
                   case ".":
                       coCham = false;
                       break;
               }
               input = input.substring(0, input.length() - 1);
               txtInput.setText(input);
           }
        });
    }

    // Ý tưởng: Chỉ đặt lại trạng thái
    private void SuKienC() {
        btnC.setOnClickListener(v -> DatLaiTrangThai());
    }

    // Ý tưởng: đặt lại trạng thái + xoá hết lịch sử phép tính
    private void SuKienAC() {
        btnAC.setOnClickListener(v -> {
            DatLaiTrangThai();
            historyList.clear();
            adapter.notifyDataSetChanged();
        });
    }

    // Ý tưởng:
    // input về 0 và txtResult về rỗng - ẩn đi
    // Khởi tạo các cờ về false
    private void DatLaiTrangThai() {
        txtInput.setText("0");
        txtResult.setText("");
        txtResult.setVisibility(TextView.INVISIBLE);
        KhoiTaoCacCo();
    }

    // Sự kiện toán tử
    // Ý tưởng:
    // TH1: Nếu người dùng nhập 1 phép tính rồi ấn dấu '=':
    // Lấy kết quả hiện tại làm input + 'operator'
    // TH2: Người dùng nhập 1 phép tính rồi nhấn 1 toán tử khác
    // Đầu tiên sẽ kiểm tra các cờ phép tính có phải là true hay không => nếu có thực hiện phép tính
    // Sau đó thục hiện việc gắn toán tử
    // Set lại các cờ về lại false v cờ của operator hiện tại là true
    // Cuối cùng nếu cờ lỗi được bật => set lại là 0 + với operator hiện tại
    // Riêng phép nhân và chia thì chỉ set về 0
    private void SuKienToanTu() {
        btnCong.setOnClickListener(v -> {
            if(coBang) {
                String result = txtResult.getText().toString().replace("=","").trim();
                String inputView = result + "+";
                txtInput.setText(inputView);
                txtResult.setText("");
                txtResult.setVisibility(TextView.INVISIBLE);

            } else {
                if (coCong || coTru || coNhan || coChia) {
                    ThucHienPhepTinh();
                }
                ThucHienToanTu('+');
            }
            coCong = true;
            coTru = false;
            coNhan = false;
            coChia = false;
            coCongTru = false;
            coCham = false;
            coBang = false;
            if(coLoi) {
                txtInput.setText("0+");
                coLoi = false;
                txtResult.setVisibility(TextView.INVISIBLE);
            }
        });
        btnTru.setOnClickListener(v -> {
            if(coBang) {
                String result = txtResult.getText().toString().replace("=","").trim();
                String inputView = result + "-";
                txtInput.setText(inputView);
                txtResult.setText("");
            } else {
                if (coCong || coTru || coNhan || coChia) {
                    ThucHienPhepTinh();
                }
                ThucHienToanTu('-');
            }
            coCong = false;
            coTru = true;
            coNhan = false;
            coChia = false;
            coCongTru = false;
            coCham = false;
            coBang = false;
            if(coLoi) {
                txtInput.setText("0-");
                coLoi = false;
                txtResult.setVisibility(TextView.INVISIBLE);
            }
            });
        btnNhan.setOnClickListener(v -> {
            if(coBang) {
                String result = txtResult.getText().toString().replace("=","").trim();
                String inputView = result + "x";
                txtInput.setText(inputView);
                txtResult.setText("");
                txtResult.setVisibility(TextView.INVISIBLE);
            } else {
                if (coCong || coTru || coNhan || coChia) {
                    ThucHienPhepTinh();
                }
                ThucHienToanTu('x');
            }
            coCong = false;
            coTru = false;
            coNhan = true;
            coChia = false;
            coCongTru = false;
            coCham = false;
            coBang = false;
            if(coLoi) {
                String text = "0x";
                txtInput.setText(text);
                coLoi = false;
                txtResult.setVisibility(TextView.INVISIBLE);
            }
        });
        btnChia.setOnClickListener(v -> {
            if(coBang) {
                String result = txtResult.getText().toString().replace("=","").trim();
                String inputView = result + "/";
                txtInput.setText(inputView);
                txtResult.setText("");
                txtResult.setVisibility(TextView.INVISIBLE);
            } else {
                if (coCong || coTru || coNhan || coChia) {
                    ThucHienPhepTinh();
                }
                ThucHienToanTu('/');
            }
            coCong = false;
            coTru = false;
            coNhan = false;
            coChia = true;
            coCongTru = false;
            coCham = false;
            coBang = false;
            if(coLoi) {
                txtInput.setText("0/");
                coLoi = false;
                txtResult.setVisibility(TextView.INVISIBLE);
            }
        });
    }

    // ý tưởng:
    // Lấy input nhập hiện tại
    // Với mỗi toán tử tương ứng ta tìm vị trí xuất hiện của toán tử trong phép tính
    // Sau đó thực hiện cắt 2 chuỗi thông qua vị trí toán tử
    // Cuối cùng chuẩn hoá
    // Nếu như input2 là 0 => là 1 phép tính 25= hoặc
    // Thực hiện phép tính tương ứng
    // Nếu như coBang là true => set kq vào txtResult và hiện txtResult lên
    // Ngược lại thì chỉ set kết qủa vào lại txtInput
    private void ThucHienPhepTinh() {
        String input = txtInput.getText().toString();
        boolean coThemLichSu = true;
        double result = 0;
        if(coCong) {
            int index = TimViTriKiTuToanTu('+', input);
            String input1 = input.substring(0, index);
            String input2 = input.substring(index + 1);
            if(input2.isEmpty()) {
                coThemLichSu = false;
            }
            double valueInput1 = ChuanHoa(input1);
            double valueInput2 = ChuanHoa(input2);
            result = valueInput1 + valueInput2;
            if(!coBang) {
                txtInput.setText(String.valueOf(result));
            } else {
                txtResult.setText(String.format("= %s", result));
                txtResult.setVisibility(TextView.VISIBLE);
            }
        } else if (coTru) {
            int index = TimViTriKiTuToanTu('-', input);
            String input1 = input.substring(0, index);
            String input2;
            // TH đặt biệt
            // Nếu coCongTru là true ta sẽ cắt từ vị trí indext trở về sau: Ví dụ 25-(-32) kết quả cắt cho input2 => -32)
            // Ngược lại Là 1 phép trừ bth => cắt từ vị trí kết tiếp: Ví dụ 1-2 => input2: 2
            if(coCongTru) {
                input2 = input.substring(index);
            } else {
                input2 = input.substring(index + 1);
            }
            if(input2.isEmpty()) {
                coThemLichSu = false;
            }
            double valueInput1 = ChuanHoa(input1);
            double valueInput2 = ChuanHoa(input2);
            result = valueInput1 - valueInput2;
            if(!coBang) {
                txtInput.setText(String.valueOf(result));
            } else {
                txtResult.setText(String.format("= %s", result));
                txtResult.setVisibility(TextView.VISIBLE);
            }
        } else if (coNhan) {
            int index = TimViTriKiTuToanTu('x', input);
            String input1 = input.substring(0, index);
            String input2 = input.substring(index + 1);
            if(input2.isEmpty()) {
                coThemLichSu = false;
            }
            double valueInput1 = ChuanHoa(input1);
            double valueInput2 = ChuanHoa(input2);
            result = valueInput1 * valueInput2;
            if(!coBang) {
                txtInput.setText(String.valueOf(result));
            } else {
                txtResult.setText(String.format("= %s", result));
                txtResult.setVisibility(TextView.VISIBLE);
            }

        } else if (coChia) {
            int index = TimViTriKiTuToanTu('/', input);
            String input1 = input.substring(0, index);
            String input2 = input.substring(index + 1);
            if (input2.isEmpty()) {
                coThemLichSu = false;
            }
            double valueInput1 = ChuanHoa(input1);
            double valueInput2 = ChuanHoa(input2);
            // Nếu mẫu số là 0 => set lỗi hiện lên giao diện
            // Ngược lại => thực hiện phép chia như bth
            if(valueInput2 == 0) {
                String resultView = "Không thể chia cho 0";
                txtResult.setText(resultView);
                coLoi = true;
                txtResult.setVisibility(TextView.VISIBLE);
            } else {
                result = valueInput1 / valueInput2;
                if(!coBang) {
                    txtInput.setText(String.valueOf(result));
                } else {
                    txtResult.setText(String.format("= %s", result));
                    txtResult.setVisibility(TextView.VISIBLE);
                }
            }
        }
        if(!coLoi) {
            if (coThemLichSu || coBang) {
                SetHistory(input, String.format("= %s", result));
            }
        } else {
            if(coThemLichSu) {
                SetHistory(input, "Không thể chia cho 0");
            }
        }
    }

    // Ý tưởng:
    // Khởi tạo giá trị mặc định nếu người dùng không nhập đủ giá trị
    // + - => 0
    // x / => 1
    // replace các kí hiệu đặc biệt
    // sau đó kiểm tra xem có kí hiệu chấm 0 => Nếu sau dấu . không có value nào => thêm 0
    // Cuối cùng convert String to double
    private double ChuanHoa(String inputValue) {
        double result;
        if (coCong || coTru) {
            result = 0;
        } else {
            result = 1;
        }
        inputValue = inputValue.replace("-(", "");
        inputValue = inputValue.replace("(", "");
        inputValue = inputValue.replace(")", "");
        if (!inputValue.isEmpty()) {
            String lastCharacter = inputValue.substring(inputValue.length() - 1);
            if(lastCharacter.equals(".")) {
                inputValue += "0";
            }
            result = Double.parseDouble(inputValue);
        }
        return result;
    }

    // Ý tưởng:
    // Ta sẽ duyệt từ đầu đến cuối chuỗi
    // Tạo 1 mảng lưu trữ vị trí của các toán tử xuất hiện
    // Nếu mảng toán tử >= 2 ta return vị trí chính giữa
    // Ngược lại return vị trí đầu tiên
    // Nếu không có toán tử nào => return -1
    private int TimViTriKiTuToanTu(char operator, String input) {
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == operator) {
               index.add(i);
            }
        }
        if(index.size() >= 2) {
            return index.get(1);
        } else if(index.size() == 1){
            return index.get(0);
        } else {
            return -1;
        }
    }

    // Ý tưởng:
    // Lấy chuỗi phép tính hiện tại
    // Lấy vị trí kí tự cuối cùng
    // Nếu là 1 toán tử => ta thay thế toán tử đó
    // Ngược lại ta append toán tử vào sau chuỗi
    private void ThucHienToanTu(char operator) {
        String input = txtInput.getText().toString();
        String lastCharacter = input.substring(input.length() - 1);
        if(lastCharacter.equals("+") || lastCharacter.equals("-") ||
                lastCharacter.equals("x") || lastCharacter.equals("/")) {
            input = input.substring(0, input.length() - 1);
        }
        input = input + operator;
        txtInput.setText(input);
    }

    // Sự kiện click số
    private void SuKienClickSo() {
        btn0.setOnClickListener(v -> ThucHienClickSo("0"));
        btn1.setOnClickListener(v -> ThucHienClickSo("1"));
        btn2.setOnClickListener(v -> ThucHienClickSo("2"));
        btn3.setOnClickListener(v -> ThucHienClickSo("3"));
        btn4.setOnClickListener(v -> ThucHienClickSo("4"));
        btn5.setOnClickListener(v -> ThucHienClickSo("5"));
        btn6.setOnClickListener(v -> ThucHienClickSo("6"));
        btn7.setOnClickListener(v -> ThucHienClickSo("7"));
        btn8.setOnClickListener(v -> ThucHienClickSo("8"));
        btn9.setOnClickListener(v -> ThucHienClickSo("9"));
    }
    // Ý tưởng:
    // Kiểm tra xem người dùng có click bằng hay phép tính lỗi không
    // Nếu đúng => Đặt lại trạng thái về ban đầu và gắn giá trị mới
    // Ngược lại => Nếu đang ở mặc định (value = 0) => set input = value ngược lại append value
    private void ThucHienClickSo(String value) {
        String input = txtInput.getText().toString();
        if(coBang || coLoi) {
            DatLaiTrangThai();
            txtInput.setText(value);
        }
        else {
            if(input.equals("0")) {
                txtInput.setText(value);
            } else {
                // apeend value có 2 TH:
                // TH1 nếu không phải số thực => append bình thường
                // TH2 là số thực
                // Tìm vị trí ')' => sự kiện +/-
                // Nếu có ta thực hiện chèn value vào trước ')'
                // Ngược lại thì append value vào sau input hiện tại
                if(!coCham) {
                    txtInput.append(value);
                } else {
                    int vt = TimViTriKiTuToanTu(')', input);
                    if (vt != -1) {
                        input = input.substring(0, vt);
                        input = input + value;
                        input = input + ")";
                    } else {
                        input = input + value;
                    }
                    txtInput.setText(input);
                }
            }
        }
    }

    private void SetHistory(String input, String result) {
        History history = new History(input, result);
        historyList.add(history);
        adapter.notifyDataSetChanged();
    }

    private void KhoiTaoCacCo() {
        coCong = false;
        coTru = false;
        coNhan = false;
        coChia = false;
        coBang = false;
        coCongTru = false;
        coLoi = false;
        coCham = false;
    }

    private void KhoiTaoLichSu() {
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(this, R.layout.item_history, historyList);
        lsvHistory.setAdapter(adapter);
    }

    private void AnhXa() {
        txtInput = findViewById(R.id.txtInput);
        txtResult = findViewById(R.id.txtResult);
        btnAC = findViewById(R.id.btnAC);
        btnC = findViewById(R.id.btnC);
        btnDelete = findViewById(R.id.btnDelete);
        btnChia = findViewById(R.id.btnChia);
        btnCham = findViewById(R.id.btnCham);
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btnCong = findViewById(R.id.btnCong);
        btnTru = findViewById(R.id.btnTru);
        btnNhan = findViewById(R.id.btnNhan);
        btnCongTru = findViewById(R.id.btnCongTru);
        btnBang = findViewById(R.id.btnBang);
        lsvHistory = findViewById(R.id.lsvHistory);
    }
}