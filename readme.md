Bài tập 1

Một phần mềm muốn có tính năng đăng ký cho người dùng mới.
Các thông tin để làm key xác thực người dùng:
- SDT với format: 84xxxxxxxx . Điện thoại tuân thủ đúng chuẩn của thế giới bắt đầu bằng số 84 và mỗi số điện thoại sẽ có 11 chứ số: Ví dụ 84982573860. Khi khách hàng nhập vào +84 hoặc bắt đầu bằng 0 => auto chuyển về 84
- Khi khách hàng đăng ký, sẽ có gửi OTP về sdt của khách hàng (Fake thôi, giá sử gửi otp thành công đi)
  OTP bao gồm 6 chữ số random, có thời hạn sống 3p.

Sau khi khách hàng được gửi OTP, thì sau 120s khách mới được gửi OTP tiếp (resend). Mỗi ngày khách được gửi tối đa 5 OTP.

Với mỗi OTP, khách được nhập sai tối đa 5 lần, nếu nhập sai lần thứ 5 => xoá phiên giao dịch khách hàng đăng ký không thành công.



- Sau khi khách hàng xác thực OTP thành công, lúc này khách hàng được cập nhật mật khẩu. Mật khẩu có rule là ít nhất 8 chữ số, bắt buộc phải có chữ và ít nhất 1 số, 1 kí tự đặc biệt, 1 chữ viết hoa.




  ---------------------------------------

Yêu cầu :
1. Học viên viết SRS
   - Sử dụng sequendiagram vẽ api foollow
   - Vẽ sơ đồ thực hiện.
2. Coding xong gửi lên git
3. Think về các yếu tố phi chức năng: perfomance của hệ thống , an ninh an toàn.



Khuyến khích học viên viết Gửi otp qua queue (Sử dụng rabbitmq hoặc queue nào đó) . Viết 1 service chỉ để nghe xong chả lzi