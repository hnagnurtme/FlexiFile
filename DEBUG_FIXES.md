# Debug & Fixes cho Profile Features

## Các lỗi đã fix:

### ✅ 1. Avatar không fit trong vòng tròn
**Vấn đề**: Khi upload ảnh avatar, ảnh không fit đúng trong vòng tròn, bị vỡ layout.

**Giải pháp**: Đã thêm CSS để ảnh fit đúng:
```css
.avatar {
    overflow: hidden;
    position: relative;
}

.avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    position: absolute;
    top: 0;
    left: 0;
}
```

**File đã sửa**: [profile.jsp:116-138](src/main/webapp/jsp/profile.jsp#L116-L138)

---

### 🔍 2. Lịch sử thanh toán không hiển thị (ĐANG DEBUG)

**Vấn đề**: Payment history đã có trong Firebase nhưng không hiển thị trên trang profile.

**Debug logging đã thêm**:

#### ProfileServlet.java
```java
System.out.println("DEBUG: User ID: " + user.getId());
System.out.println("DEBUG: Payment History count: " + paymentHistory.size());
for (PaymentHistory ph : paymentHistory) {
    System.out.println("DEBUG: Payment - OrderID: " + ph.getOrderId()
        + ", Status: " + ph.getStatus()
        + ", Amount: " + ph.getAmount());
}
```

#### PaymentHistoryDAO.java
```java
System.out.println("DEBUG PaymentHistoryDAO: Getting payments for userId: " + userId);
System.out.println("DEBUG: Query returned " + querySnapshot.size() + " documents");
System.out.println("DEBUG: Mapped payment - ID: " + payment.getId()
    + ", OrderID: " + payment.getOrderId()
    + ", UserId: " + payment.getUserId());
System.out.println("DEBUG: Returning " + paymentList.size() + " payments after sorting");
```

---

## Cách debug lỗi Payment History:

### Bước 1: Chạy ứng dụng
```bash
mvn clean package
# Deploy to Tomcat hoặc chạy server
```

### Bước 2: Truy cập trang profile
1. Login vào hệ thống
2. Vào `/profile`
3. Kiểm tra console/log của server

### Bước 3: Phân tích log

**Case 1: Không thấy log "DEBUG PaymentHistoryDAO"**
- Vấn đề: PaymentBO không được gọi
- Kiểm tra: ProfileServlet có được mapping đúng không?
- Solution: Verify servlet mapping trong web.xml hoặc annotation

**Case 2: "Query returned 0 documents"**
- Vấn đề: userId không khớp với data trong Firebase
- Kiểm tra:
  ```
  - User ID trong log
  - userId field trong Firebase payment_history collection
  ```
- Solution: Verify userId matching

**Case 3: "Query returned X documents" nhưng vẫn không hiển thị**
- Vấn đề: JSP không nhận được data
- Kiểm tra: request.setAttribute có được gọi không?
- Solution: Verify request forwarding

**Case 4: Firestore Index Error**
- Log sẽ show: "DEBUG: Index not found, using in-memory sorting"
- Đây là bình thường, code sẽ fallback sang in-memory sorting
- No action needed

---

## Các điểm cần kiểm tra trong Firebase:

### 1. Collection name
```
Collection: payment_history
```

### 2. Document structure
Mỗi payment document phải có:
```javascript
{
  userId: "exact_user_id_string",  // PHẢI KHỚP với user.getId()
  orderId: "...",
  amount: number,
  status: "SUCCESS" | "FAILED",
  planType: "BASIC" | "PREMIUM" | "PRO",
  additionalConverts: number,
  paymentDate: Timestamp,
  // ... other fields
}
```

### 3. Kiểm tra userId matching
**Cách kiểm tra**:
1. Lấy userId từ log: `DEBUG: User ID: xxxxx`
2. Vào Firebase Console
3. Filter collection `payment_history` với: `userId == "xxxxx"`
4. Xem có documents nào không?

**Lưu ý**: userId phải khớp CHÍNH XÁC (case-sensitive)

---

## Quick Fixes nếu không hiển thị:

### Fix 1: Verify userId trong payment records
```java
// Trong VNPayReturnServlet hoặc nơi tạo payment history
String userId = user.getId();
System.out.println("Creating payment with userId: " + userId);

PaymentHistory payment = new PaymentHistory();
payment.setUserId(userId);  // PHẢI ĐÚNG userId
// ...
```

### Fix 2: Tạo lại payment record với userId đúng
```java
// Script tạm để fix data (chạy 1 lần)
public void fixPaymentHistory() {
    // Lấy userId đúng từ user đang login
    String correctUserId = currentUser.getId();

    // Update tất cả payment records
    paymentHistoryDAO.updateUserIdForPayments(oldUserId, correctUserId);
}
```

### Fix 3: Test với mock data
Thêm vào ProfileServlet để test:
```java
// DEBUG: Tạo mock payment để test
if (paymentHistory == null || paymentHistory.isEmpty()) {
    System.out.println("DEBUG: Creating mock payment for testing");
    PaymentHistory mock = new PaymentHistory();
    mock.setOrderId("TEST001");
    mock.setAmount(100000);
    mock.setStatus("SUCCESS");
    mock.setPlanType("PREMIUM");
    mock.setAdditionalConverts(200);
    mock.setPaymentDate(new Date());

    paymentHistory = new ArrayList<>();
    paymentHistory.add(mock);

    request.setAttribute("paymentHistory", paymentHistory);
}
```

---

## Debugging Checklist:

- [ ] Server đã restart sau khi compile?
- [ ] User đã login thành công?
- [ ] Log hiển thị user ID đúng?
- [ ] Firebase có data trong collection `payment_history`?
- [ ] Field `userId` trong Firebase khớp với log?
- [ ] PaymentHistoryDAO được khởi tạo đúng?
- [ ] Request.setAttribute được gọi?
- [ ] JSP import đúng class PaymentHistory?
- [ ] Không có exception trong log?

---

## Testing Payment History Display:

### Test 1: Manual data check
```bash
# Kiểm tra trong Firebase Console:
1. Vào Firestore Database
2. Mở collection "payment_history"
3. Check có documents nào có userId = [your_user_id]
4. Verify structure đúng
```

### Test 2: Create test payment
```java
// Tạo payment mới để test
1. Vào trang /payment
2. Chọn gói BASIC (50,000 VND)
3. Thanh toán qua VNPay sandbox
4. Quay lại /profile
5. Check có hiển thị không
```

### Test 3: Direct JSP test
Thêm vào profile.jsp để test:
```jsp
<%
// DEBUG OUTPUT
out.println("<!-- DEBUG: User ID = " + user.getId() + " -->");
out.println("<!-- DEBUG: Payment History = " + paymentHistory + " -->");
if (paymentHistory != null) {
    out.println("<!-- DEBUG: Payment count = " + paymentHistory.size() + " -->");
}
%>
```

---

## Expected Log Output (Success case):

```
DEBUG PaymentHistoryDAO: Getting payments for userId: abc123xyz
DEBUG: Query returned 3 documents
DEBUG: Mapped payment - ID: payment1, OrderID: PREMIUM1234567890, UserId: abc123xyz
DEBUG: Mapped payment - ID: payment2, OrderID: BASIC1234567891, UserId: abc123xyz
DEBUG: Mapped payment - ID: payment3, OrderID: PRO1234567892, UserId: abc123xyz
DEBUG: Returning 3 payments after sorting
DEBUG: User ID: abc123xyz
DEBUG: Payment History count: 3
DEBUG: Payment - OrderID: PREMIUM1234567890, Status: SUCCESS, Amount: 100000.0
DEBUG: Payment - OrderID: BASIC1234567891, Status: SUCCESS, Amount: 50000.0
DEBUG: Payment - OrderID: PRO1234567892, Status: SUCCESS, Amount: 200000.0
```

---

## Next Steps:

1. **Chạy server và check logs** - Quan trọng nhất!
2. **So sánh userId** từ log với Firebase
3. **Verify data structure** trong Firebase
4. **Test với 1 payment mới** nếu cần

Sau khi có log output, chúng ta sẽ biết chính xác vấn đề ở đâu!
