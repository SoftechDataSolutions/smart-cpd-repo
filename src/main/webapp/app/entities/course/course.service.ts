import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICourse } from 'app/shared/model/course.model';

type EntityResponseType = HttpResponse<ICourse>;
type EntityArrayResponseType = HttpResponse<ICourse[]>;

@Injectable({ providedIn: 'root' })
export class CourseService {
    private resourceUrl = SERVER_API_URL + 'api/courses';
    private resourceCheckUrl = SERVER_API_URL + 'api/check/courses';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/courses';

    constructor(private http: HttpClient) {}

    create(course: ICourse): Observable<EntityResponseType> {
        return this.http.post<ICourse>(this.resourceUrl, course, { observe: 'response' });
    }

    check(id: number, customer: number): Observable<boolean> {
        return this.http.post<boolean>(`${this.resourceCheckUrl}/${id}`, customer);
    }

    update(course: ICourse): Observable<EntityResponseType> {
        return this.http.put<ICourse>(this.resourceUrl, course, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ICourse>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICourse[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICourse[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
