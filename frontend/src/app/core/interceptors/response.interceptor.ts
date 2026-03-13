import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs';

export const responseInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    map((event: any) => {
      if (event instanceof HttpResponse) {
        const body = event.body;
        if (body && body?.data !== undefined && body?.success !== undefined) {
          return event.clone({ body: body.data });
        }
      }
      return event;
    })
  );
};