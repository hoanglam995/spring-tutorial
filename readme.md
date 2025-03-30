# Bài tập 1

Một phần mềm muốn có tính năng đăng ký cho người dùng mới.
Các thông tin để làm key xác thực người dùng:
- SDT với format: 84xxxxxxxx . Điện thoại tuân thủ đúng chuẩn của thế giới bắt đầu bằng số 84 và mỗi số điện thoại sẽ có 11 chứ số: Ví dụ 84982573860. Khi khách hàng nhập vào +84 hoặc bắt đầu bằng 0 => auto chuyển về 84
- Khi khách hàng đăng ký, sẽ có gửi OTP về sdt của khách hàng (Fake thôi, giá sử gửi otp thành công đi)
  OTP bao gồm 6 chữ số random, có thời hạn sống 3p.

Sau khi khách hàng được gửi OTP, thì sau 120s khách mới được gửi OTP tiếp (resend). Mỗi ngày khách được gửi tối đa 5 OTP.

Với mỗi OTP, khách được nhập sai tối đa 5 lần, nếu nhập sai lần thứ 5 => xoá phiên giao dịch khách hàng đăng ký không thành công.



- Sau khi khách hàng xác thực OTP thành công, lúc này khách hàng được cập nhật mật khẩu. Mật khẩu có rule là ít nhất 8 chữ số, bắt buộc phải có chữ và ít nhất 1 số, 1 kí tự đặc biệt, 1 chữ viết hoa.




  ---------------------------------------

**Yêu cầu :**
1. Học viên viết SRS
   - Sử dụng sequendiagram vẽ api foollow
   - Vẽ sơ đồ thực hiện.
2. Coding xong gửi lên git
3. Think về các yếu tố phi chức năng: perfomance của hệ thống , an ninh an toàn.



Khuyến khích học viên viết Gửi otp qua queue (Sử dụng rabbitmq hoặc queue nào đó) . Viết 1 service chỉ để nghe xong chả lzi

-----------------------------------------------------------------------

**Tóm tắt:**
- SĐT:
    - 11 chữ số nếu bắt đầu bằng 84
    - 12 chữ số nếu bắt đầu +84
    - 10 chữ số nếu bắt đầu 0
    - Khi lưu tự chuyển đổi về 11 chữ số và bắt đầu 84
      => Chỉ validate đúng định dạng 11 số, FE validate và tự chuyển định dạng gửi lên về đúng 11 số
- OTP:
    - Gồm 6 chữ số random
    - Thời hạn sống 3p(180s)
    - Gửi lại sau 120s
    - Mỗi ngày gửi tối đa 5 OTP
    - Mỗi phiên đăng ký, OTP được nhập sai tối đa 5 lần, nhập sai lần thứ 5 => xoá phiên giao dịch khách hàng đăng ký không thành công
- Password:
    - Có ít nhất 8 chữ số
    - Ít nhất 1 chữ viết hoa
    - Ít nhất 1 chữ viết thường
    - Ít nhất 1 số
    - Ít nhất 1 ký tự đặc biệt

**Phân tích:**
Các API cần triển khai:
- API đăng ký => input là số điện thoại => output là OTP code
Logic: check validate định dạng, kiểm tra số đt đã đăng ký chưa hoặc đang đăng ký không. Nếu có thì hiển thị thông báo, nếu không thì tạo cache lưu thông tin số điện thoại, mã OTP
- API gửi lại mã OTP => input là số điện thoại => output là OTP code
Logic: check validate tồn tại Sđt, check số lần gửi trong ngày hôm nay, check thời gian gửi gần nhất, check thời gian sống của otp, nếu hết hạn thì tạo lại và trả về
- API xác thực OTP => input là mã số điện thoại và mã OTP => output: token để thay đổi mật khẩu
Logic: check validate số điện thoại, check thời gian sống OTP, check đúng OTP, nếu không đúng OTP thì update lại số lần nhập sai, nếu đúng trả về token để thay đổi mk
- API Thay đổi mật khẩu lần đầu => input là số điện thoại và token thay đổi mật khẩu => output: thông báo đăng ký thành công
Logic: check token thay đổi mk, check validate password lưu thông tin xuống database

**Biểu đồ Sequance**

1. API Đăng Ký

```mermaid
---
title: API Đăng Ký
---
sequenceDiagram
    participant User
    participant System
    participant Database
    participant Redis
    participant Queue Service
    
    User->>System:Nhập số điện thoại

    System->>System:Validate SĐT

    opt SDT không đúng định dạng
        System-->>User: Thống báo lỗi sai định dạng
    end

    System->>Database: Lấy dữ liệu database
    Database-->>System: Thành công
    
    opt SĐT tồn tại trong database và chưa được kích hoạt
    System-->>User: Thống báo lỗi số điện thoại đang chờ kích hoạt và chuyển hướng người dùng đến trang xác thực OTP
    end
    
    opt SĐT tồn tại trong database và đã được kích hoạt
    System-->>User: Thống báo lỗi tài khoản đã được đăng ký
    end
    
    System->>Database: Lưu dữ liệu vào database
    Database-->>System: Thành công
    
    System->>System: Tạo mã OTP, thời gian có thể gửi lại OTP, số lần gửi OTP trong ngày = 1, số lần nhập sai OTP = 0
    
    System->>Redis: Lưu mã OTP, thời gian có thể gửi lại, số lần gửi OTP trong ngày, số lần nhập sai OTP vào cache
    Redis-->>System: Thành công
    
    System->>Queue Service: Tạo Job gửi OTP về số điện thoại đăng ký
    Queue Service-->>System: Thành công.
    
    System-->>User: Thống báo thành công, OTP đã được gửi về SĐT
```

2. API Gửi Lại OTP

```mermaid
---
title: Api Gửi Lại OTP
---
sequenceDiagram
  participant User
  participant System
  participant Database
  participant Redis
  participant Queue Service

  User->>System:Click gửi lại OTP

  System->>System:Validate SĐT

  opt SDT không đúng định dạng
    System-->>User: Thống báo lỗi sai định dạng
  end

  System->>Database: Lấy dữ liệu database
  Database-->>System: Thành công
  
  opt SĐT không tồn tại trong database
    System-->>User: Thống báo lỗi số điện thoại chưa được đăng ký
  end
  
  opt SĐT tồn tại trong database và đã được kích hoạt
    System-->>User: Thống báo lỗi tài khoản đã được đăng ký
  end
  
  System->>Redis: Lấy số lần gửi OTP trong ngày từ cache
  Redis-->>System: Thành công
  
  opt SĐT vượt quá số lần gửi OTP trong ngày
    System-->>User: Thống báo lỗi vượt quá số lần gửi mã OTP trong ngày
  end
  
  System->>Redis: Lấy thời gian có thể gửi lại OTP từ cache
  Redis-->>System: Thành công
  
  opt SĐT chưa đủ thời gian chờ sau mỗi lần gửi lại
    System-->>User: Thống báo lỗi chưa đủ thời gian chờ gửi lại OTP
  end
  
  System->>Redis: Lấy OTP cũ từ cache
  Redis-->>System: Thành công
  
  opt OTP cũ không có hoặc đã hết hạn
    System->>System: Tạo lại mã OTP mới
    
    System->>Redis: Lưu OTP mới vào cache
    Redis-->>System: Thành công
  end
  
  System->>Redis: Lưu thời gian có thể gửi lại và số lần gửi lại OTP tặng 1 vào cache
  Redis-->>System: Thành công
  
  System->>Queue Service: Tạo Job gửi OTP về số điện thoại đăng ký
  Queue Service-->>System: Thành công
  
  System-->>User: Thống báo thành công, OTP đã được gửi về SĐT
```

3. API Xác Thực OTP

```mermaid
---
title: Api Xác thực OTP
---
sequenceDiagram
  participant User
  participant System
  participant Database
  participant Redis

  User->>System: Nhập mã OTP và xác thực

  System->>System:Validate SĐT

  opt SDT không đúng định dạng
    System-->>User: Thống báo lỗi sai định dạng
  end

  System->>Database: Lấy dữ liệu database
  Database-->>System: Thành công
  
  opt SĐT không tồn tại trong database
    System-->>User: Thống báo lỗi số điện thoại chưa được đăng ký
  end
  
  opt SĐT tồn tại trong database và đã được kích hoạt
    System-->>User: Thống báo lỗi tài khoản đã được đăng ký
  end
  
  System->>System: Validate OTP
  
  opt OTP đúng định dạng
    System->>Redis: Lấy OTP từ cache
    Redis-->>System: Thành công
    
    opt OTP còn hiệu lực
    System->>System: Kiểm tra mã OTP
    
      opt OTP trùng nhau
      System->>System: Tạo token đổi mật khẩu
      
      System->>Redis: Lưu token đổi mật khẩu vào cache
      Redis-->>System: Thành công
      
      System-->>User: Thông báo xác thực OTP thành công, trả lại token đổi mật khẩu
      end
    end
  end
  
  System->>Redis: Lấy số lần nhập sai OTP
  Redis-->>System: Thành công
  
  alt Số lần nhập sai vượt quá số lần cho phép
    System->>Database: Xoá dữ liệu đăng ký trong database
    Database-->>System: Thành công
    
    System->>Redis: Xoá OTP, thời gian có thể gửi lại, số lần gửi OTP trong ngày, số lần nhập sai OTP trong cache
    Redis-->>System: Thành công
  else
    System->>Redis: Update số lần nhập sai OTP trong cache
    Redis-->>System: Thành công 
  end
  System-->>User: Thông báo lỗi xác thực OTP
```

4. API Kích Hoạt Và Thay Đổi Mật Khẩu Lần Đầu

```mermaid
---
title: API Kích Hoạt Và Thay Đổi Mật Khẩu Lần Đầu
---
sequenceDiagram
  participant User
  participant System
  participant Database
  participant Redis

  User->>System: Click link thay đổi mật khẩu và nhập mật khẩu mới

  System->>System:Validate SĐT

  opt SDT không đúng định dạng
    System-->>User: Thống báo lỗi sai định dạng
  end

  System->>Database: Lấy dữ liệu database
  Database-->>System: Thành công
  
  opt SĐT không tồn tại trong database
    System-->>User: Thống báo lỗi số điện thoại chưa được đăng ký
  end
  
  opt SĐT tồn tại trong database và đã được kích hoạt
    System-->>User: Thống báo lỗi tài khoản đã được đăng ký
  end
  
  System->>System: Validate Mật khẩu
  
  opt Mật khẩu không đúng định dạng
    System-->>User: Thông báo lỗi mật khẩu không đúng định dạng
  end
  
  System->>Redis: Lấy token đổi mật khẩu trong cache
  Redis-->>System: Thành công
  
  opt Token đổi mật khẩu không trùng nhau
    System-->>User: Thông báo lỗi xác thực token thay đổi mật khẩu
  end
  
  System->>Database: Cập nhật mật khẩu mới và trạng thái kích hoạt tài khoản trong database
  Database-->>System: Thành công
  
  System->>Redis: Xoá OTP, thời gian có thể gửi lại, số lần gửi OTP trong ngày, số lần nhập sai OTP trong cache
  Redis-->>System: Thành công
  
  System-->>User: Thông báo tài khoản đã đăng ký thành công
```

**Biểu đồ Flow**

1. API Đăng Ký

```mermaid
---
title: API Đăng Ký
---
flowchart
    START([Bắt Đầu])-->1(1.Validate SDT đúng định dạng, không tồn tại trong database)-->C1{SĐT hợp lệ}
    C1--NO-->END([Kết thúc])
    C1--YES-->2(2.Lưu thông tin vào database)
    2-->3(3.Tạo mã OTP, thời gian có thể gửi lại OTP, Số lâ gửi OTP trong ngày, Số lần nhập sai OTP)-->4(4.Lưu dữ liệu vào Cache)
    4-->5(5.Tạo job gửi OTP về SĐT)-->END
```

2. API Gửi Lại OTP

```mermaid
---
title: API Gửi Lại OTP
---
flowchart
    START([Bắt Đầu])-->1(1.Validate SDT đúng định dạng, có bản ghi trong database với trạng thái chưa kích hoạt)-->C1{SĐT hợp lệ}
    C1--NO-->END([Kết thúc])
    C1--YES-->2(2.Check số lần gửi OTP trong ngày)-->C2{Chưa vượt quá 5 lần}
    C2--No-->END([Kết thúc])
    C2--YES-->3(3.So sánh thời gian có thể gửi lại OTP và thời gian hiện tại)-->C3{Thời gian hiện tại lớn hơn}
    C3--No-->END([Kết thúc])
    C3--YES-->4(4.Lấy OTP từ cache)-->5[5.Check tồn tại OTP]-->C4{OTP tồn tại}
    C4--NO-->5.1(5.1.Tạo mã OTP mới)
    5.1-->5.2(5.2.Lưu OTP vào Cache)
    C4--YES-->6(6.Update thời gian có thể gửi lại và số lần gửi lại trong ngày vào cache)
    5.2-->6
    6-->7(7.Tạo job gửi OTP về SĐT)-->END
```

3. API Xác Thực OTP

```mermaid
---
title: API Xác Thực OTP
---
flowchart
    START([Bắt Đầu])-->1(1.Validate SDT đúng định dạng, có bản ghi trong database với trạng thái chưa kích hoạt)-->C1{SĐT hợp lệ}
    C1--NO-->END([Kết thúc])
    C1--YES-->2(2.Validate OTP)-->C2{đúng định dạng, còn hiệu lực, và trùng khớp }
    C2--YES-->3.1(3.Tạo token đổi mật khẩu và lưu vào cache)
    3.1-->END([Kết thúc])
    C2--NO-->3.2(3.Kiểm tra số lần nhập sai OTP)-->C3{Vượt quá 5 lần}
    C3--YES-->4.1(4.Xoá thông tin đăng ký trong database và thông tin OTP trong cache)-->END
    C3--NO-->4.2(4.Update số lần nhập sai OTP)-->END
```

4. API Kích Hoạt Và Thay Đổi Mật Khẩu Lần Đầu

```mermaid
---
title: API Kích Hoạt Và Thay Đổi Mật Khẩu Lần Đầu
---
flowchart
    START([Bắt Đầu])-->1(1.Validate SDT đúng định dạng, có bản ghi trong database với trạng thái chưa kích hoạt)-->C1{SĐT hợp lệ}
    C1--NO-->END([Kết thúc])
    C1--YES-->2(2.Validate Password)-->C2{Password đúng định dạng}
    C2--NO-->END([Kết thúc])
    C2--YES-->3(3.Validate Token thay đổi Password)-->C3{Token thay đổi password trùng khớp}
    C3--NO-->END([Kết thúc])
    C3--YES-->4(4.Update password và trạng thái tài khoản đã kích hoạt)
    4-->5(5.Xoá thông tin OTP trên cache)-->END
    
```