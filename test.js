import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    standard: {
      executor: 'constant-vus',
      vus: 200,
      duration: '10s',
      exec: 'standardTest',
    },
    executor: {
      executor: 'constant-vus',
      vus: 200,
      duration: '10s',
      exec: 'executorTest',
      startTime: '15s',
    },
    virtual: {
      executor: 'constant-vus',
      vus: 200,
      duration: '10s',
      exec: 'virtualTest',
      startTime: '30s',
    },
    reactive: {
      executor: 'constant-vus',
      vus: 200,
      duration: '10s',
      exec: 'reactiveTest',
      startTime: '45s',
    },
  },
};

const BASE_URL = 'http://localhost:8080/api';

export function standardTest() {
  const res = http.get(`${BASE_URL}/standard`);
  check(res, { 'status was 200': (r) => r.status == 200 });
}

export function executorTest() {
  const res = http.get(`${BASE_URL}/executor`);
  check(res, { 'status was 200': (r) => r.status == 200 });
}

export function virtualTest() {
  const res = http.get(`${BASE_URL}/virtual`);
  check(res, { 'status was 200': (r) => r.status == 200 });
}

export function reactiveTest() {
  const res = http.get(`${BASE_URL}/reactive`);
  check(res, { 'status was 200': (r) => r.status == 200 });
}
