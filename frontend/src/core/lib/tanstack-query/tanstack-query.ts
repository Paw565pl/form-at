import {
  defaultShouldDehydrateQuery,
  environmentManager,
  QueryClient,
} from "@tanstack/react-query";
import { HttpStatusCode, isAxiosError } from "axios";

const nonRetryableErrorCodes = new Set([
  HttpStatusCode.Unauthorized,
  HttpStatusCode.Forbidden,
  HttpStatusCode.NotFound,
  HttpStatusCode.Gone,
  HttpStatusCode.BadRequest,
  HttpStatusCode.TooManyRequests,
]);

const MAX_RETRY_COUNT = 3;

const makeQueryClient = () => {
  return new QueryClient({
    defaultOptions: {
      queries: {
        // With SSR, we usually want to set some default staleTime
        // above 0 to avoid refetching immediately on the client
        staleTime: 1000 * 60 * 5, // 5 minutes
        retry: (failureCount, error) => {
          const isNonRetryableError =
            isAxiosError(error) &&
            nonRetryableErrorCodes.has(error.status ?? 0);
          if (isNonRetryableError || failureCount > MAX_RETRY_COUNT) {
            return false;
          }

          return true;
        },
      },
      dehydrate: {
        // include pending queries in dehydration
        shouldDehydrateQuery: (query) =>
          defaultShouldDehydrateQuery(query) ||
          query.state.status === "pending",
        shouldRedactErrors: () => {
          // We should not catch Next.js server errors
          // as that's how Next.js detects dynamic pages
          // so we cannot redact them.
          // Next.js also automatically redacts errors for us
          // with better digests.
          return false;
        },
      },
    },
  });
};

let browserQueryClient: QueryClient | null = null;

export const getQueryClient = () => {
  // Server: always make a new query client
  if (environmentManager.isServer()) return makeQueryClient();

  // Browser: make a new query client if we don't already have one
  // This is very important, so we don't re-make a new client if React
  // suspends during the initial render. This may not be needed if we
  // have a suspense boundary BELOW the creation of the query client
  browserQueryClient ??= makeQueryClient();
  return browserQueryClient;
};
