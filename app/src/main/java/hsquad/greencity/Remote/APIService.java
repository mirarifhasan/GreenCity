package hsquad.greencity.Remote;

import hsquad.greencity.Model.MyResponse;
import hsquad.greencity.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAALrwlSF4:APA91bHIVqq-9rjcO0cxMTJ_VcN2L3TCokAbXKyQm6sJ0cmH2x04kpPhwgIMNhAcxxmMO7ZMazY2XuAmJ9egzzrUBZYqLdtschVxRKbBCqPmdbBhxpZH_8COGicPJPP3jXMrB4Ndz_Z0"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
